package org.pac4j.core.authorization.authorizer;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.*;

/**
 * Tests {@link IsFullyAuthenticatedAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class IsFullyAuthenticatedAuthorizerTests extends IsAuthenticatedAuthorizerTests {

    protected Authorizer newAuthorizer() {
        return new IsFullyAuthenticatedAuthorizer();
    }

    @Test
    public void testAnonymousProfileRedirectionUrl() {
        profiles.add(new AnonymousProfile());
        ((IsFullyAuthenticatedAuthorizer) authorizer).setRedirectionUrl(PAC4J_URL);
        TestsHelper.expectException(() -> authorizer.isAuthorized(MockWebContext.create(), profiles), RequiresHttpAction.class, "user should be fully authenticated");
    }

    @Test
    public void testCommonRmeProfile() throws RequiresHttpAction {
        profile.setRemembered(true);
        profiles.add(profile);
        assertFalse(authorizer.isAuthorized(null, profiles));
    }
}
