package org.pac4j.jwt.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.text.ParseException;

/**
 * JWK helper.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class JWKHelper {

    /**
     * Build the secret from the JWK JSON.
     *
     * @param json the json
     * @return the secret
     */
    public static String buildSecretFromJwk(final String json) {
        CommonHelper.assertNotBlank("json", json);

        try {
            final var octetSequenceKey = OctetSequenceKey.parse(json);
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
            final var rsaKey = RSAKey.parse(json);
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
            final var ecKey = ECKey.parse(json);
            return ecKey.toKeyPair();
        } catch (final JOSEException | ParseException e) {
            throw new TechnicalException(e);
        }
    }
}
