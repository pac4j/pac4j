package org.pac4j.core.authorization.authorizer;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.assertFalse;

/**
 * Tests {@link IsRememberedAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class IsRememberedAuthorizerTests extends IsAuthenticatedAuthorizerTests {

    protected Authorizer newAuthorizer() {
        return new IsRememberedAuthorizer<>();
    }

    @Override
    protected boolean isRemembered() {
        return true;
    }

    @Override
    @Test
    public void testAnonymousProfileRedirectionUrl() {
        profiles.add(new AnonymousProfile());
        ((IsRememberedAuthorizer) authorizer).setRedirectionUrl(PAC4J_URL);
        TestsHelper.expectException(() -> authorizer.isAuthorized(MockWebContext.create(), profiles), RequiresHttpAction.class, "user should be remembered");
    }

    @Test
    public void testCommonRmeProfile() throws RequiresHttpAction {
        profile.setRemembered(false);
        profiles.add(profile);
        assertFalse(authorizer.isAuthorized(null, profiles));
    }
}
