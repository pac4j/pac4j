package org.pac4j.jwt.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

import java.io.UnsupportedEncodingException;

/**
 * Direct encryption configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class DirectEncryptionConfiguration extends InitializableObject implements EncryptionConfiguration {

    private String secret;

    private EncryptionMethod method = EncryptionMethod.A256GCM;

    public DirectEncryptionConfiguration() {}

    public DirectEncryptionConfiguration(final String secret) {
        this.secret = secret;
    }

    public DirectEncryptionConfiguration(final String secret, final EncryptionMethod method) {
        this.secret = secret;
        this.method = method;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotBlank("secret", secret);
        CommonHelper.assertNotNull("method", method);
    }

    @Override
    public String encrypt(final SignedJWT signedJWT) {
        init();

        // Create JWE object with signed JWT as payload
        final JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.DIR, this.method).contentType("JWT").build(),
                new Payload(signedJWT));

        try {
            // Perform encryption
            jweObject.encrypt(new DirectEncrypter(this.secret.getBytes("UTF-8")));
        } catch (final UnsupportedEncodingException | JOSEException e) {
            throw new TechnicalException(e);
        }

        // Serialise to JWE compact form
        return jweObject.serialize();
    }

    @Override
    public SignedJWT decrypt(EncryptedJWT encryptedJWT) {
        init();

        try {
            final JWEObject jweObject = encryptedJWT;
            jweObject.decrypt(new DirectDecrypter(this.secret.getBytes("UTF-8")));

            // Extract payload
            return jweObject.getPayload().toSignedJWT();

        } catch (final UnsupportedEncodingException | JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public EncryptionMethod getMethod() {
        return method;
    }

    public void setMethod(EncryptionMethod method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "secret", "[protected]", "method", method);
    }
}
