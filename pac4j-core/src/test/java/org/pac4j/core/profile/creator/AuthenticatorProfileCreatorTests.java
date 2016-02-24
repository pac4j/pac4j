package org.pac4j.core.profile.creator;

import org.junit.Test;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This class tests the {@link AuthenticatorProfileCreator}.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class AuthenticatorProfileCreatorTests implements TestsConstants {

    private final static AuthenticatorProfileCreator creator = new AuthenticatorProfileCreator();

    @Test
    public void testReturnNoProfile() {
        assertNull(creator.create(new TokenCredentials(TOKEN, CLIENT_NAME)));
    }

    @Test
    public void testReturnProfile() {
        final UserProfile profile = new UserProfile();
        final Credentials credentials = new TokenCredentials(TOKEN, CLIENT_NAME);
        credentials.setUserProfile(profile);
        final UserProfile profile2 = creator.create(credentials);
        assertEquals(profile, profile2);
    }
}
