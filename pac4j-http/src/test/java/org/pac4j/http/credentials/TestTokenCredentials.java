package org.pac4j.http.credentials;

import junit.framework.TestCase;

/**
 * General test cases for TokenCredentials
 *
 * @author Jacob Severson
 * @since  1.8.0
 */
public class TestTokenCredentials extends TestCase {

    public void testClearTokenCredentials() {
        TokenCredentials tokenCredentials = new TokenCredentials("testToken", "testClient");
        tokenCredentials.clear();
        assertNull(tokenCredentials.getClientName());
        assertNull(tokenCredentials.getToken());
        assertNull(tokenCredentials.getUserProfile());
    }
}
