package org.pac4j.core.authorization.authorizer;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests {@link IsAnonymousAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class IsAnonymousAuthorizerTests implements TestsConstants {

    private IsAnonymousAuthorizer authorizer;

    private List<UserProfile> profiles;

    @Before
    public void setUp() {
        authorizer = new IsAnonymousAuthorizer();
        profiles = new ArrayList<>();
    }

    @Test
    public void testNoProfile() {
        assertTrue(authorizer.isAuthorized(null, new MockSessionStore(), profiles));
    }

    @Test
    public void testAnonymousProfile() {
        profiles.add(new AnonymousProfile());
        assertTrue(authorizer.isAuthorized(null, new MockSessionStore(), profiles));
    }

    @Test
    public void testAnonProfileTwoProfiles() {
        profiles.add(new AnonymousProfile());
        profiles.add(new CommonProfile());
        assertFalse(authorizer.isAuthorized(null, new MockSessionStore(), profiles));
    }

    @Test
    public void testTwoAnonProfiles() {
        profiles.add(new AnonymousProfile());
        profiles.add(new AnonymousProfile());
        assertTrue(authorizer.isAuthorized(null, new MockSessionStore(), profiles));
    }

    @Test
    public void testCommonProfile() {
        profiles.add(new CommonProfile());
        assertFalse(authorizer.isAuthorized(null, new MockSessionStore(), profiles));
    }

    @Test
    public void testCommonProfileRedirectionUrl() {
        profiles.add(new CommonProfile());
        authorizer.setRedirectionUrl(PAC4J_URL);
        TestsHelper.expectException(() -> authorizer.isAuthorized(MockWebContext.create(), new MockSessionStore(), profiles),
            HttpAction.class, "Performing a 302 HTTP action");
    }
}
