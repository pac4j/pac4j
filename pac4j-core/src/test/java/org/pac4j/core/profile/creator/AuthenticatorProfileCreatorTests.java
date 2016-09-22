package org.pac4j.core.profile.creator;

import org.junit.Test;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.HttpAction;
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
    public void testReturnNoProfile() throws HttpAction {
        assertNull(AuthenticatorProfileCreator.INSTANCE.create(new TokenCredentials(TOKEN, CLIENT_NAME), null));
    }

    @Test
    public void testReturnProfile() throws HttpAction {
        final CommonProfile profile = new CommonProfile();
        final Credentials credentials = new TokenCredentials(TOKEN, CLIENT_NAME);
        credentials.setUserProfile(profile);
        final CommonProfile profile2 = AuthenticatorProfileCreator.INSTANCE.create(credentials, null);
        assertEquals(profile, profile2);
    }

    private static final class MyCommonProfile extends CommonProfile {
        public MyCommonProfile() { }
    }

    @Test
    public void testReturnNewProfile() throws HttpAction {
        final CommonProfile profile = new CommonProfile();
        profile.setId(ID);
        profile.addAttribute(KEY, VALUE);
        profile.setRemembered(false);
        profile.addRole(NAME);
        profile.addPermission(VALUE);
        profile.setClientName(CLIENT_NAME);
        final Credentials credentials = new TokenCredentials(TOKEN, CLIENT_NAME);
        credentials.setUserProfile(profile);
        final AuthenticatorProfileCreator creator = new AuthenticatorProfileCreator();
        creator.setProfileFactory(MyCommonProfile::new);
        final CommonProfile profile2 = creator.create(credentials, null);
        assertTrue(profile2 instanceof MyCommonProfile);
        assertEquals(profile.getId(), profile2.getId());
        assertEquals(profile.getAttributes(), profile2.getAttributes());
        assertEquals(profile.getRoles(), profile2.getRoles());
        assertEquals(profile.getPermissions(), profile2.getPermissions());
        assertEquals(profile.isRemembered(), profile2.isRemembered());
        assertEquals(profile.getClientName(), profile2.getClientName());
    }
}
