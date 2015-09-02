package org.pac4j.gae.credentials;

import com.google.appengine.api.users.User;
import junit.framework.TestCase;
import org.pac4j.core.util.TestsConstants;

/**
 * General test cases for GaeUserCredentials.
 *
 * @author  Jacob Severson
 * @since   1.8.0
 */
public class TestGaeUserCredentials extends TestCase implements TestsConstants {

    public void testClearGaeUserCredentials() {
        GaeUserCredentials gaeUserCredentials = new GaeUserCredentials ();
        gaeUserCredentials.setUser(new User("testEmail@test.com", "test.com", "testUserId"));
        gaeUserCredentials.clear();
        assertNull(gaeUserCredentials.getUser());
        assertNull(gaeUserCredentials.getClientName());
    }
}
