package org.pac4j.oidc.profile;

import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import junit.framework.TestCase;

/**
 * General test cases for OidcProfiles.
 *
 * @author Jacob Severson
 * @since  1.8.0
 */
public class TestOidcProfile extends TestCase {

    public void testClearProfile() {
        OidcProfile profile = new OidcProfile(new BearerAccessToken());
        profile.clear();
        assertNull(profile.getAccessToken());
    }
}
