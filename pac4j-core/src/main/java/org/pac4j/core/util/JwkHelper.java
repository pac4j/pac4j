package org.pac4j.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.KeyStoreException;
import java.text.ParseException;
import java.util.UUID;

/**
 * Helper for JWK.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Slf4j
public class JwkHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static JWK loadJwkFromOrCreateJwks(final JwksProperties jwksProperties) {
        LOGGER.debug("Loading signingKey from JWKS");
        val jwksResource = jwksProperties.getJwksResource();
        val kid = jwksProperties.getKid();
        if (!jwksResource.exists()) {
            if (!jwksResource.isFile()) {
                throw new TechnicalException("Cannot create JWKS resource which is not a file: " + jwksResource);
            }
            LOGGER.debug("No signingKey found in JWKS: generating one");
            try {
                // not supported for federation yet (SDK 11.31.1):
                // new OctetKeyPairGenerator(Curve.Ed25519).keyID(buildKid(kid)).keyUse(KeyUse.SIGNATURE).generate();
                val generatedKey = new RSAKeyGenerator(2048)
                    .keyUse(KeyUse.SIGNATURE)
                    .keyID(kid)
                    .generate();

                val path = jwksResource.getFile().toPath().toString();
                saveJwkPrivate(generatedKey, path);

                return generatedKey;
            } catch (final JOSEException | IOException e) {
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
                savePrivateJwk(target, jwkSetJson);
            }
        } catch (final IOException e) {
            throw new TechnicalException(e);
        }
    }

    private static void savePrivateJwk(final Path target, final String content) throws IOException {
        try {
            val permissions = PosixFilePermissions.fromString("rw-------");
            val attributes = PosixFilePermissions.asFileAttribute(permissions);
            if (!Files.exists(target)) {
                Files.createFile(target, attributes);
            }
            Files.writeString(target, content, StandardOpenOption.TRUNCATE_EXISTING);
            Files.setPosixFilePermissions(target, permissions);
        } catch (final UnsupportedOperationException e) {
            LOGGER.warn("POSIX file permissions are not supported for path: {}", target);
            Files.writeString(target, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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

    public static String buildKid(final String originalKid) {
        if (originalKid != null) {
            return originalKid;
        } else {
            return UUID.randomUUID().toString();
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
        val header = new JWSHeader.Builder(algorithm)
            .type(new JOSEObjectType(type))
            .keyID(key.getKeyID())
            .build();

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
}
