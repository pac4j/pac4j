package org.pac4j.core.credentials.password;

import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the {@link BasicSaltedSha512PasswordEncoder}.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class BasicSaltedSha512PasswordEncoderTests implements TestsConstants {

    private final static String SALTED_PASSWORD = "fa6a2185b3e0a9a85ef41ffb67ef3c1fb6f74980f8ebf970e4e72e353ed9537d593083c201dfd6e43e1c8a7aac2bc8dbb119c7dfb7d4b8f131111395bd70e97f";

    @Test(expected = TechnicalException.class)
    public void testNoSalt() {
        final PasswordEncoder encoder = new BasicSaltedSha512PasswordEncoder();
        encoder.encode(PASSWORD);
    }

    @Test
    public void testEncoding() {
        final PasswordEncoder encoder = new BasicSaltedSha512PasswordEncoder(SALT);
        final String encodedPwd = encoder.encode(PASSWORD);
        assertEquals(SALTED_PASSWORD, encodedPwd);
    }
}
