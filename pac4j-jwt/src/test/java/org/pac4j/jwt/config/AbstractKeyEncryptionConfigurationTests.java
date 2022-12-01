package org.pac4j.jwt.config;

import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.TestsConstants;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * A common class for key encryption tests.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public abstract class AbstractKeyEncryptionConfigurationTests implements TestsConstants {

    protected KeyPair buildKeyPair() {
        val algorithm = getAlgorithm();
        try {
            val keyGen = KeyPairGenerator.getInstance(algorithm);
            if (CommonHelper.areEquals(algorithm, "RSA")) {
                keyGen.initialize(2048);
            }
            return keyGen.generateKeyPair();
        } catch (final NoSuchAlgorithmException e) {
            throw new TechnicalException(e);
        }
    }

    protected abstract String getAlgorithm();
}
