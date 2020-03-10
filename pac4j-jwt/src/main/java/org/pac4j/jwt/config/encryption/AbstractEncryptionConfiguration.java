package org.pac4j.jwt.config.encryption;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.InitializableObject;

import java.text.ParseException;

/**
 * Abstract encryption configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public abstract class AbstractEncryptionConfiguration extends InitializableObject implements EncryptionConfiguration {

    protected JWEAlgorithm algorithm;

    protected EncryptionMethod method;

    @Override
    public String encrypt(final JWT jwt) {
        init();

        if (jwt instanceof SignedJWT) {
            // Create JWE object with signed JWT as payload
            final JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(this.algorithm, this.method).contentType("JWT").build(),
                    new Payload((SignedJWT) jwt));

            try {
                // Perform encryption
                jweObject.encrypt(buildEncrypter());
            } catch (final JOSEException e) {
                throw new TechnicalException(e);
            }

            // Serialise to JWE compact form
            return jweObject.serialize();
        } else {
            // create header
            final JWEHeader header = new JWEHeader(this.algorithm, this.method);

            try {
                // encrypted jwt
                final EncryptedJWT encryptedJwt = new EncryptedJWT(header, jwt.getJWTClaimsSet());

                // Perform encryption
                encryptedJwt.encrypt(buildEncrypter());

                // serialize
                return encryptedJwt.serialize();
            } catch (final JOSEException | ParseException e) {
                throw new TechnicalException(e);
            }
        }
    }

    /**
     * Build the appropriate encrypter.
     *
     * @return the appropriate encrypter
     */
    protected abstract JWEEncrypter buildEncrypter();

    @Override
    public void decrypt(final EncryptedJWT encryptedJWT) throws JOSEException {
        init();

        // decrypt
        encryptedJWT.decrypt(buildDecrypter());
    }

    /**
     * Build the appropriate decrypter.
     *
     * @return the appropriate decrypter
     */
    protected abstract JWEDecrypter buildDecrypter();

    public JWEAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(final JWEAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public EncryptionMethod getMethod() {
        return method;
    }

    public void setMethod(final EncryptionMethod method) {
        this.method = method;
    }
}
