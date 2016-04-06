package org.pac4j.core.authorization.authorizer;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the "is authenticated" authorizers.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class AbstractIsAuthenticatedAuthorizerTests implements TestsConstants {

    protected Authorizer authorizer;

    protected List<CommonProfile> profiles;

    protected CommonProfile profile;

    @Before
    public void setUp() {
        authorizer = newAuthorizer();
        profiles = new ArrayList<>();
        profile = new CommonProfile();
        profile.setRemembered(isRemembered());
    }

    protected abstract Authorizer newAuthorizer();

    protected boolean isRemembered() {
        return false;
    }

    @Test
    public void testNoProfile() throws RequiresHttpAction {
        assertFalse(authorizer.isAuthorized(null, profiles));
    }

    @Test
    public void testAnonymousProfile() throws RequiresHttpAction {
        profiles.add(new AnonymousProfile());
        assertFalse(authorizer.isAuthorized(null, profiles));
    }

    @Test
    public void testCommonProfileTwoProfiles() throws RequiresHttpAction {
        profiles.add(new AnonymousProfile());
        profiles.add(profile);
        assertTrue(authorizer.isAuthorized(null, profiles));
    }

    @Test
    public void testCommonProfile() throws RequiresHttpAction {
        profiles.add(profile);
        assertTrue(authorizer.isAuthorized(null, profiles));
    }
}
