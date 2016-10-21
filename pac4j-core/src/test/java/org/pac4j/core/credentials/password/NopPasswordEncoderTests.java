package org.pac4j.core.credentials.password;

import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * This class tests the {@link NopPasswordEncoder}.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class NopPasswordEncoderTests implements TestsConstants {

    private final static PasswordEncoder encoder = new NopPasswordEncoder();

    @Test
    public void testEncoding() {
        final String encodedPwd = encoder.encode(PASSWORD);
        assertEquals(PASSWORD, encodedPwd);
    }

    @Test
    public void testMatching() {
        assertTrue(encoder.matches(NAME, NAME));
        assertFalse(encoder.matches(NAME, null));
        assertFalse(encoder.matches(NAME, VALUE));
        assertFalse(encoder.matches(null, VALUE));
    }
}
