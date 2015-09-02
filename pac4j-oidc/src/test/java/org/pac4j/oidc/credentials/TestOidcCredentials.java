package org.pac4j.oidc.credentials;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import junit.framework.TestCase;

/**
 * General test cases for OidcCredentials.
 *
 * @author Jacob Severson
 * @since  1.8.0
 */
public class TestOidcCredentials extends TestCase {

    public void testClearOidcCredentials() {
        OidcCredentials oidcCredentials = new OidcCredentials(new AuthorizationCode());
        oidcCredentials.setClientName("testClient");
        oidcCredentials.clear();
        assertNull(oidcCredentials.getClientName());
        assertNull(oidcCredentials.getCode());
    }
}
