package org.pac4j.jwt.config.encryption;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.InitializableObject;

import java.text.ParseException;

/**
 * Abstract encryption configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@ToString
@Getter
@Setter
public abstract class AbstractEncryptionConfiguration extends InitializableObject implements EncryptionConfiguration {

    protected JWEAlgorithm algorithm;

    protected EncryptionMethod method;

    /** {@inheritDoc} */
    @Override
    public String encrypt(final JWT jwt) {
        init();

        if (jwt instanceof SignedJWT signedJWT) {
            // Create JWE object with signed JWT as payload
            val jweObject = new JWEObject(
                    new JWEHeader.Builder(this.algorithm, this.method).contentType("JWT").build(),
                    new Payload(signedJWT));

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
            val header = new JWEHeader(this.algorithm, this.method);

            try {
                // encrypted jwt
                val encryptedJwt = new EncryptedJWT(header, jwt.getJWTClaimsSet());

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

    /** {@inheritDoc} */
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
}
