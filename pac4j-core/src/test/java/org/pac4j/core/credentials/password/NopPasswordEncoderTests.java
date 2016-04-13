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

    @Test
    public void test() {
        final PasswordEncoder encoder = new NopPasswordEncoder();
        final String encodedPwd = encoder.encode(PASSWORD);
        assertEquals(PASSWORD, encodedPwd);
    }
}
