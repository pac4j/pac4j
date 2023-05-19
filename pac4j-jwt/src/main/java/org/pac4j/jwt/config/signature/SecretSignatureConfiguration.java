package org.pac4j.jwt.config.signature;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
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

    /**
     * <p>Constructor for SecretSignatureConfiguration.</p>
     */
    public SecretSignatureConfiguration() {
        algorithm = JWSAlgorithm.HS256;
    }

    /**
     * <p>Constructor for SecretSignatureConfiguration.</p>
     *
     * @param secret a {@link String} object
     */
    public SecretSignatureConfiguration(final String secret) {
        this(secret.getBytes(UTF_8));
    }

    /**
     * <p>Constructor for SecretSignatureConfiguration.</p>
     *
     * @param secret an array of {@link byte} objects
     */
    public SecretSignatureConfiguration(final byte[] secret) {
        this();
        this.secret = Arrays.copyOf(secret,secret.length);
    }

    /**
     * <p>Constructor for SecretSignatureConfiguration.</p>
     *
     * @param secret a {@link String} object
     * @param algorithm a {@link JWSAlgorithm} object
     */
    public SecretSignatureConfiguration(final String secret, final JWSAlgorithm algorithm) {
        this(secret.getBytes(UTF_8),algorithm);
    }

    /**
     * <p>Constructor for SecretSignatureConfiguration.</p>
     *
     * @param secret an array of {@link byte} objects
     * @param algorithm a {@link JWSAlgorithm} object
     */
    public SecretSignatureConfiguration(final byte[] secret, final JWSAlgorithm algorithm) {
        this.secret = Arrays.copyOf(secret,secret.length);
        this.algorithm = algorithm;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("algorithm", algorithm);
        CommonHelper.assertNotNull("secret", secret);

        if (!supports(this.algorithm)) {
            throw new TechnicalException("Only the HS256, HS384 and HS512 algorithms are supported for HMac signature");
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final JWSAlgorithm algorithm) {
        return algorithm != null && MACVerifier.SUPPORTED_ALGORITHMS.contains(algorithm);
    }

    /** {@inheritDoc} */
    @Override
    public SignedJWT sign(final JWTClaimsSet claims) {
        init();

        try {
            JWSSigner signer = new MACSigner(this.secret);
            val signedJWT = new SignedJWT(new JWSHeader(algorithm), claims);
            signedJWT.sign(signer);
            return signedJWT;
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean verify(final SignedJWT jwt) throws JOSEException {
        init();

        JWSVerifier verifier = new MACVerifier(this.secret);
        return jwt.verify(verifier);
    }

    /**
     * <p>Getter for the field <code>secret</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getSecret() {
        return new String(secret,UTF_8);
    }

    /**
     * <p>Setter for the field <code>secret</code>.</p>
     *
     * @param secret a {@link String} object
     */
    public void setSecret(final String secret) {
        this.secret = secret.getBytes(UTF_8);
    }

    /**
     * <p>getSecretBytes.</p>
     *
     * @return an array of {@link byte} objects
     */
    public byte[] getSecretBytes() {
        return  Arrays.copyOf(secret,secret.length);
    }

    /**
     * <p>setSecretBytes.</p>
     *
     * @param secretBytes an array of {@link byte} objects
     */
    public void setSecretBytes(final byte[] secretBytes) {
        this.secret = Arrays.copyOf(secretBytes,secretBytes.length);
    }

    /**
     * <p>getSecretBase64.</p>
     *
     * @return a {@link String} object
     */
    public String getSecretBase64() {
        return Base64.encode(secret).toString();
    }

    /**
     * <p>setSecretBase64.</p>
     *
     * @param secret a {@link String} object
     */
    public void setSecretBase64(final String secret) {
        this.secret = new Base64(secret).decode();
    }
}
