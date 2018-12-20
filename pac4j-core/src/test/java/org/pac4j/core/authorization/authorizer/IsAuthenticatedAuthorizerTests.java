package org.pac4j.core.authorization.authorizer;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link IsAuthenticatedAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class IsAuthenticatedAuthorizerTests implements TestsConstants {

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

    protected Authorizer newAuthorizer() {
        return new IsAuthenticatedAuthorizer();
    }

    protected boolean isRemembered() {
        return false;
    }

    @Test
    public void testNoProfile() {
        assertFalse(authorizer.isAuthorized(null, profiles));
    }

    @Test
    public void testAnonymousProfile() {
        profiles.add(new AnonymousProfile());
        assertFalse(authorizer.isAuthorized(null, profiles));
    }

    @Test
    public void testCommonProfileTwoProfiles() {
        profiles.add(new AnonymousProfile());
        profiles.add(profile);
        assertTrue(authorizer.isAuthorized(null, profiles));
    }

    @Test
    public void testCommonProfile() {
        profiles.add(profile);
        assertTrue(authorizer.isAuthorized(null, profiles));
    }

    @Test
    public void testAnonymousProfileRedirectionUrl() {
        profiles.add(new AnonymousProfile());
        ((IsAuthenticatedAuthorizer) authorizer).setRedirectionUrl(PAC4J_URL);
        TestsHelper.expectException(() -> authorizer.isAuthorized(MockWebContext.create(), profiles), HttpAction.class,
            "Performing a 302 HTTP action");
    }
}
