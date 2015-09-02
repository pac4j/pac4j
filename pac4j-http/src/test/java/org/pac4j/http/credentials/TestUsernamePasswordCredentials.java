package org.pac4j.http.credentials;

import junit.framework.TestCase;

/**
 * General test cases for UsernamePasswordCredentials.
 *
 * @author Jacob Severson
 * @since  1.8.0
 */
public class TestUsernamePasswordCredentials extends TestCase {

    public void testClearUsernamePasswordCredentials() {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                "testUsername",
                "testPassword",
                "testClient"
        );
        credentials.clear();
        assertNull(credentials.getUserProfile());
        assertNull(credentials.getClientName());
        assertNull(credentials.getPassword());
        assertNull(credentials.getUsername());
    }
}
