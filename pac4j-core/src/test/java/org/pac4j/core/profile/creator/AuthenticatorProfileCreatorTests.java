package org.pac4j.core.profile.creator;

import org.junit.Test;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * This class tests the {@link AuthenticatorProfileCreator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class AuthenticatorProfileCreatorTests implements TestsConstants {

    @Test
    public void testReturnNoProfile() {
        assertFalse(
            AuthenticatorProfileCreator.INSTANCE.create(
                new TokenCredentials(TOKEN), null
            ).isPresent()
        );
    }

    @Test
    public void testReturnProfile() {
        final CommonProfile profile = new CommonProfile();
        final Credentials credentials = new TokenCredentials(TOKEN);
        credentials.setUserProfile(profile);
        final CommonProfile profile2 = (CommonProfile) AuthenticatorProfileCreator.INSTANCE.create(credentials, null).get();
        assertEquals(profile, profile2);
    }
}
