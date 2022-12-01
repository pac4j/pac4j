package org.pac4j.jwt.config.signature;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * HMac signature configuration: http://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-hmac
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class SecretSignatureConfiguration extends AbstractSignatureConfiguration {

    private byte[] secret;

    public SecretSignatureConfiguration() {
        algorithm = JWSAlgorithm.HS256;
    }

    public SecretSignatureConfiguration(final String secret) {
        this(secret.getBytes(UTF_8));
    }

    public SecretSignatureConfiguration(final byte[] secret) {
        this();
        this.secret = Arrays.copyOf(secret,secret.length);
    }

    public SecretSignatureConfiguration(final String secret, final JWSAlgorithm algorithm) {
        this(secret.getBytes(UTF_8),algorithm);
    }

    public SecretSignatureConfiguration(final byte[] secret, final JWSAlgorithm algorithm) {
        this.secret = Arrays.copyOf(secret,secret.length);
        this.algorithm = algorithm;
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("algorithm", algorithm);
        CommonHelper.assertNotNull("secret", secret);

        if (!supports(this.algorithm)) {
            throw new TechnicalException("Only the HS256, HS384 and HS512 algorithms are supported for HMac signature");
        }
    }

    @Override
    public boolean supports(final JWSAlgorithm algorithm) {
        return algorithm != null && MACVerifier.SUPPORTED_ALGORITHMS.contains(algorithm);
    }

    @Override
    public SignedJWT sign(final JWTClaimsSet claims) {
        init();

        try {
            val signer = new MACSigner(this.secret);
            val signedJWT = new SignedJWT(new JWSHeader(algorithm), claims);
            signedJWT.sign(signer);
            return signedJWT;
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    public boolean verify(final SignedJWT jwt) throws JOSEException {
        init();

        val verifier = new MACVerifier(this.secret);
        return jwt.verify(verifier);
    }

    public String getSecret() {
        return new String(secret,UTF_8);
    }

    public void setSecret(final String secret) {
        this.secret = secret.getBytes(UTF_8);
    }

    public byte[] getSecretBytes() {
        return  Arrays.copyOf(secret,secret.length);
    }

    public void setSecretBytes(final byte[] secretBytes) {
        this.secret = Arrays.copyOf(secretBytes,secretBytes.length);
    }

    public String getSecretBase64() {
        return Base64.encode(secret).toString();
    }

    public void setSecretBase64(final String secret) {
        this.secret = new Base64(secret).decode();
    }
}
