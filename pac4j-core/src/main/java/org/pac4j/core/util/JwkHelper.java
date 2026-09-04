package org.pac4j.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.keystore.loading.KeyStoreUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.text.ParseException;

/**
 * Helper for JWK.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Slf4j
public class JwkHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Resolve the signing key from the JWKS if it is defined, from the keystore otherwise, creating one for
     * the default algorithm when the JWKS resource does not exist yet.
     *
     * @param jwks the JWKS properties, may be null
     * @param keystore the keystore properties, may be null
     * @return the signing key
     */
    public static JWK resolveSigningKey(final JwksProperties jwks, final KeystoreProperties keystore) {
        return resolveSigningKey(jwks, keystore, DEFAULT_CREATED_KEY_ALGORITHM);
    }

    /**
     * Resolve the signing key from the JWKS if it is defined, from the keystore otherwise.
     *
     * <p>Either source carries the whole material: a JWK holds the private key and, in its "x5c" member, the
     * certificate chain. A keystore is what a certification authority hands over, a JWKS is the usual shape
     * of a key without any certificate, but both can hold both.</p>
     *
     * @param jwks the JWKS properties, may be null
     * @param keystore the keystore properties, may be null
     * @param createdKeyAlgorithm the algorithm of the key created when the JWKS resource does not exist yet
     * @return the signing key
     */
    public static JWK resolveSigningKey(final JwksProperties jwks, final KeystoreProperties keystore,
                                        final JWSAlgorithm createdKeyAlgorithm) {
        if (jwks != null && jwks.isDefined()) {
            return loadJwkFromOrCreateJwks(jwks, createdKeyAlgorithm);
        }
        if (keystore != null && keystore.getKeystoreResource() != null) {
            return loadJwkFromOrCreateKeyStore(keystore);
        }
        throw new TechnicalException("A JWKS or a keystore is mandatory to get the signing key");
    }

    /** The algorithm of the key created when none exists yet, kept for backward compatibility. */
    public static final JWSAlgorithm DEFAULT_CREATED_KEY_ALGORITHM = JWSAlgorithm.RS256;

    public static JWK loadJwkFromOrCreateJwks(final JwksProperties jwksProperties) {
        return loadJwkFromOrCreateJwks(jwksProperties, DEFAULT_CREATED_KEY_ALGORITHM);
    }

    /**
     * Load the signing key from the JWKS, creating one for the given algorithm when the resource does not
     * exist yet. Some profiles mandate an algorithm the default RSA key would not satisfy: OpenID4VP's high
     * assurance profile requires ES256, hence a P-256 key.
     *
     * @param jwksProperties where the JWKS lives
     * @param createdKeyAlgorithm the algorithm of the key to create when none exists
     * @return the signing key
     */
    public static JWK loadJwkFromOrCreateJwks(final JwksProperties jwksProperties, final JWSAlgorithm createdKeyAlgorithm) {
        LOGGER.debug("Loading signingKey from JWKS");
        val jwksResource = jwksProperties.getJwksResource();
        val kid = jwksProperties.getKid();
        if (!jwksResource.exists()) {
            if (!jwksResource.isFile()) {
                throw new TechnicalException("Cannot create JWKS resource which is not a file: " + jwksResource);
            }
            LOGGER.debug("No signingKey found in JWKS: generating a {} one", createdKeyAlgorithm);
            try {
                val generatedKey = generateKey(createdKeyAlgorithm, kid);

                val path = jwksResource.getFile().toPath().toString();
                saveJwkPrivate(generatedKey, path);

                return generatedKey;
            } catch (final IOException e) {
                throw new TechnicalException(e);
            }
        }
        LOGGER.debug("Reading signingKey from: {}", jwksResource);
        JWKSet jwkSet = null;
        try (val is = jwksResource.getInputStream()) {
            jwkSet = JWKSet.load(is);
        } catch (final IOException | ParseException e) {
            throw new TechnicalException(e);
        }

        JWK signingJwk;
        if (kid != null) {
            signingJwk = jwkSet.getKeys().stream()
                .filter(k -> k.getKeyID().equals(kid))
                .filter(k -> KeyUse.SIGNATURE.equals(k.getKeyUse()))
                .filter(JWK::isPrivate)
                .findFirst()
                .orElseThrow(() -> new TechnicalException("No private key (" + kid + ") for signature"));
        } else {
            signingJwk = jwkSet.getKeys().stream()
                .filter(k -> KeyUse.SIGNATURE.equals(k.getKeyUse()))
                .filter(JWK::isPrivate)
                .findFirst()
                .orElseThrow(() -> new TechnicalException("No private key for signature"));
        }

        return signingJwk;
    }

    /**
     * Generate a signature key for the given algorithm, without any key identifier.
     *
     * @param algorithm the algorithm the key must be usable with
     * @return the generated key, private part included
     */
    public static JWK generateKey(final JWSAlgorithm algorithm) {
        return generateKey(algorithm, null);
    }

    /**
     * Generate a signature key for the given algorithm, identified by the given key identifier.
     *
     * @param algorithm the algorithm the key must be usable with
     * @param kid the key identifier, null to generate a key carrying none
     * @return the generated key, private part included
     */
    public static JWK generateKey(final JWSAlgorithm algorithm, final String kid) {
        try {
            // not supported for federation yet (SDK 11.31.1):
            // new OctetKeyPairGenerator(Curve.Ed25519).keyID(kid).keyUse(KeyUse.SIGNATURE).generate();
            if (JWSAlgorithm.ES256.equals(algorithm) || JWSAlgorithm.ES384.equals(algorithm)
                || JWSAlgorithm.ES512.equals(algorithm)) {
                val curve = Curve.forJWSAlgorithm(algorithm).iterator().next();
                return new ECKeyGenerator(curve).keyUse(KeyUse.SIGNATURE).keyID(kid).algorithm(algorithm).generate();
            }
            if (JWSAlgorithm.RS256.equals(algorithm) || JWSAlgorithm.RS384.equals(algorithm)
                || JWSAlgorithm.RS512.equals(algorithm) || JWSAlgorithm.PS256.equals(algorithm)
                || JWSAlgorithm.PS384.equals(algorithm) || JWSAlgorithm.PS512.equals(algorithm)) {
                return new RSAKeyGenerator(2048).keyUse(KeyUse.SIGNATURE).keyID(kid).algorithm(algorithm).generate();
            }
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
        throw new TechnicalException("Cannot generate a key for the algorithm: " + algorithm);
    }

    public static void saveJwkPrivate(final JWK key, final String path) {
        saveJwk(key, path, false);
    }

    public static void saveJwkPublic(final JWK key, final String path) {
        saveJwk(key, path, true);
    }

    private static void saveJwk(final JWK key, final String path, final boolean publicKeysOnly) {
        try {
            val jwkSet = new JWKSet(key);
            val jwkSetJson = MAPPER.writerWithDefaultPrettyPrinter()
                .writeValueAsString(jwkSet.toJSONObject(publicKeysOnly));
            LOGGER.debug("Saving key to path: {} (public only: {})", path, publicKeysOnly);
            val target = Path.of(path);
            val parent = target.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (publicKeysOnly) {
                Files.writeString(target, jwkSetJson, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } else {
                FileHelper.savePrivateFile(target, jwkSetJson);
            }
        } catch (final IOException e) {
            throw new TechnicalException(e);
        }
    }

    public static JWK loadJwkFromOrCreateKeyStore(final KeystoreProperties keystoreProperties) {
        LOGGER.debug("Loading signingKey from keystore");
        val keystoreGenerator = keystoreProperties.getKeystoreGenerator();
        if (keystoreGenerator.shouldGenerate()) {
            LOGGER.info("Generating keystore for resource: {}", keystoreProperties.getKeystoreResource());
            keystoreGenerator.generate();
        }
        val keyStoreAndAlias = KeyStoreUtils.retrieveKeyStoreAndAlias(keystoreProperties);
        val keyStore = keyStoreAndAlias.getLeft();
        val alias = keyStoreAndAlias.getRight();
        try {
            return JWK.load(keyStore, alias, keystoreProperties.getPrivateKeyPassword().toCharArray());
        } catch (final KeyStoreException | JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Determine the key algorithm.
     *
     * @param key                   the key
     * @param allowSymmetricSigning whether symmetric signing (HMac) is allowed
     * @return the algorithm
     */
    public static JWSAlgorithm determineAlgorithm(final JWK key, final boolean allowSymmetricSigning) {
        if (key.getAlgorithm() != null) {
            return JWSAlgorithm.parse(key.getAlgorithm().getName());
        }

        if (key instanceof OctetKeyPair) {
            return JWSAlgorithm.EdDSA;
        } else if (key instanceof ECKey ecKey) {
            val curve = ecKey.getCurve();
            if (curve == null) {
                throw new TechnicalException("ECKey without curve");
            }

            if (Curve.P_256.equals(curve) || Curve.SECP256K1.equals(curve)) {
                return JWSAlgorithm.ES256;
            } else if (Curve.P_384.equals(curve)) {
                return JWSAlgorithm.ES384;
            } else if (Curve.P_521.equals(curve)) {
                return JWSAlgorithm.ES512;
            } else {
                throw new TechnicalException("Unsupported EC curve: " + curve.getName());
            }
        } else if (key instanceof RSAKey rsaKey) {
            val bitLength = rsaKey.getModulus().decodeToBigInteger().bitLength();
            if (bitLength >= 4096) {
                return JWSAlgorithm.RS512;
            } else if (bitLength >= 3072) {
                return JWSAlgorithm.RS384;
            } else {
                return JWSAlgorithm.RS256;
            }
        } else if (key instanceof OctetSequenceKey || key instanceof SecretKey) {
            if (!allowSymmetricSigning) {
                throw new TechnicalException("Symmetric keys (OctetSequenceKey / SecretKey) are not allowed");
            }

            byte[] keyBytes;
            if (key instanceof OctetSequenceKey osk) {
                keyBytes = osk.toByteArray();
            } else {
                keyBytes = ((SecretKey) key).getEncoded();
                if (keyBytes == null) {
                    throw new TechnicalException("Cannot get encoded bytes from SecretKey");
                }
            }

            val keyLengthBits = keyBytes.length * 8;
            if (keyLengthBits >= 512) {
                return JWSAlgorithm.HS512;
            }
            if (keyLengthBits >= 384) {
                return JWSAlgorithm.HS384;
            }
            if (keyLengthBits >= 256) {
                return JWSAlgorithm.HS256;
            }

            throw new TechnicalException("Symmetric key too short for secure HMAC: " + keyLengthBits + " bits ");
        }

        throw new TechnicalException("Unsupported key type: " + key.getClass().getSimpleName());
    }

    public static JWSSigner determineSigner(final JWK key, final boolean allowSymmetricSigning) {
        return determineSigner(key, null, allowSymmetricSigning);
    }

    public static JWSSigner determineSigner(final JWK key, final JWSAlgorithm a, final boolean allowSymmetricSigning) {
        var alg = a;
        if (alg == null) {
            alg = determineAlgorithm(key, allowSymmetricSigning);
        }

        try {
            if (alg.getName().startsWith("HS")) {
                if (!allowSymmetricSigning) {
                    throw new TechnicalException("Can't get signer for symmetric keys");
                }
                if (key instanceof OctetSequenceKey octet) {
                    return new MACSigner(octet);
                }
                if (key instanceof SecretKey secret) {
                    return new MACSigner(secret);
                }
                throw new TechnicalException("HMAC algorithm requires OctetSequenceKey or SecretKey");
            } else if (key instanceof OctetKeyPair okp) {
                val curve = okp.getCurve();
                if (Curve.Ed25519.equals(curve)) {
                    return new Ed25519Signer(okp);
                }
                throw new TechnicalException("Unsupported EdDSA curve: " + curve);
            } else if (key instanceof ECKey ec) {
                return new ECDSASigner(ec);
            } else if (key instanceof RSAKey rsa) {
                return new RSASSASigner(rsa);
            }
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }

        throw new TechnicalException("No signer found for key type " + key.getClass().getSimpleName() + " and alg " + alg);
    }

    public static JWSVerifier determineVerifier(final JWK key, final boolean allowSymmetricSigning) {
        return determineVerifier(key, null, allowSymmetricSigning);
    }

    public static JWSVerifier determineVerifier(final JWK key, final JWSAlgorithm a, final boolean allowSymmetricSigning) {
        var alg = a;
        if (alg == null) {
            alg = determineAlgorithm(key, allowSymmetricSigning);
        }

        try {
            if (alg.getName().startsWith("HS")) {
                if (!allowSymmetricSigning) {
                    throw new TechnicalException("Can't get verifier for symmetric keys");
                }
                if (key instanceof OctetSequenceKey octet) {
                    return new MACVerifier(octet);
                }
                if (key instanceof SecretKey secret) {
                    return new MACVerifier(secret);
                }
                throw new TechnicalException("HMAC algorithm requires OctetSequenceKey or SecretKey");
            } else if (key instanceof OctetKeyPair okp) {
                val curve = okp.getCurve();
                if (Curve.Ed25519.equals(curve)) {
                    return new Ed25519Verifier(okp);
                }
                throw new TechnicalException("Unsupported EdDSA curve: " + curve);
            } else if (key instanceof ECKey ec) {
                return new ECDSAVerifier(ec);
            } else if (key instanceof RSAKey rsa) {
                return new RSASSAVerifier(rsa);
            }
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }

        throw new TechnicalException("No verifier found for key type " + key.getClass().getSimpleName() + " and alg " + alg);
    }

    public static boolean hasPrivatePart(final JWK key) {
        var hasPrivate = false;
        if (key instanceof OctetKeyPair okp) {
            hasPrivate = okp.getD() != null;
        } else if (key instanceof ECKey ec) {
            hasPrivate = ec.getD() != null;
        } else if (key instanceof RSAKey rsa) {
            hasPrivate = rsa.getPrivateExponent() != null;
        }
        return hasPrivate;
    }

    public static String buildSignedJwt(final JWTClaimsSet claims, final JWK key, final JWSAlgorithm algorithm, final String type) {
        return buildSignedJwt(claims, key, algorithm, type, false);
    }

    /**
     * Build a signed JWT, publishing in the "x5c" header the certificate chain the signing key carries.
     *
     * <p>Some protocols bind the identity of the signer to a certificate rather than to a key: OpenID4VP does
     * so with its "x509_san_dns" client identifier prefix, where the verifier is trusted through the relying
     * party access certificate its request object carries.</p>
     *
     * <p>The chain is read from the key itself, so that it can never disagree with the signature.</p>
     *
     * @param claims the claims
     * @param key the signing key
     * @param algorithm the signature algorithm
     * @param type the "typ" header
     * @param withCertificateChain whether the certificate chain of the key must be published
     * @return the serialized signed JWT
     */
    public static String buildSignedJwt(final JWTClaimsSet claims, final JWK key, final JWSAlgorithm algorithm,
                                        final String type, final boolean withCertificateChain) {
        val builder = new JWSHeader.Builder(algorithm)
            .type(new JOSEObjectType(type))
            .keyID(key.getKeyID());
        if (withCertificateChain) {
            val chain = key.getX509CertChain();
            if (chain == null || chain.isEmpty()) {
                throw new TechnicalException("No certificate chain in the signing key: " + key.getKeyID());
            }
            builder.x509CertChain(chain);
        }
        val header = builder.build();

        val signedJWT = new SignedJWT(header, claims);
        val signer = determineSigner(key, false);
        try {
            signedJWT.sign(signer);
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }

        return signedJWT.serialize();
    }

    public static String buildSignedJwt(final JWTClaimsSet claims, final JWK key, final String type) {
        val alg = determineAlgorithm(key, false);
        return buildSignedJwt(claims, key, alg, type);
    }

    /**
     * Build the secret from the JWK JSON.
     *
     * @param json the json
     * @return the secret
     */
    public static String buildSecretFromJwk(final String json) {
        CommonHelper.assertNotBlank("json", json);

        try {
            val octetSequenceKey = OctetSequenceKey.parse(json);
            return new String(octetSequenceKey.toByteArray(), "UTF-8");
        } catch (final UnsupportedEncodingException | ParseException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Build the RSA key pair from the JWK JSON.
     *
     * @param json the json
     * @return the key pair
     */
    public static KeyPair buildRSAKeyPairFromJwk(final String json) {
        CommonHelper.assertNotBlank("json", json);

        try {
            val rsaKey = RSAKey.parse(json);
            return rsaKey.toKeyPair();
        } catch (final JOSEException | ParseException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Build the EC key pair from the JWK JSON.
     *
     * @param json the json
     * @return the key pair
     */
    public static KeyPair buildECKeyPairFromJwk(final String json) {
        CommonHelper.assertNotBlank("json", json);

        try {
            val ecKey = ECKey.parse(json);
            return ecKey.toKeyPair();
        } catch (final JOSEException | ParseException e) {
            throw new TechnicalException(e);
        }
    }
}
