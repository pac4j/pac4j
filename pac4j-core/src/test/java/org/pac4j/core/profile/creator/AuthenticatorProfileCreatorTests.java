package org.pac4j.core.profile.creator;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.credentials.AuthenticationCredentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This class tests the {@link AuthenticatorProfileCreator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class AuthenticatorProfileCreatorTests implements TestsConstants {

    @Test
    public void testReturnNoProfile() {
        assertFalse(AuthenticatorProfileCreator.INSTANCE.create(null, new TokenCredentials(TOKEN)).isPresent());
    }

    @Test
    public void testReturnProfile() {
        val profile = new CommonProfile();
        final AuthenticationCredentials credentials = new TokenCredentials(TOKEN);
        credentials.setUserProfile(profile);
        val profile2 = (CommonProfile) AuthenticatorProfileCreator.INSTANCE.create(null, credentials).get();
        assertEquals(profile, profile2);
    }
}
