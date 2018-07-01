package org.pac4j.core.credentials.password;

import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * Tests {@link JBCryptPasswordEncoder}.
 *
 * @author Jerome Leleu
 * @since 3.1.0
 */
public final class JBCryptPasswordEncoderTests implements TestsConstants {

    private final JBCryptPasswordEncoder encoder = new JBCryptPasswordEncoder();

    @Test
    public void test() {
        final String hashedPwd = encoder.encode(PASSWORD);
        assertTrue(encoder.matches(PASSWORD, hashedPwd));
        assertFalse(encoder.matches(VALUE, hashedPwd));
    }
}
