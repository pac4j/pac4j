package org.pac4j.core.authorization.authorizer;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.util.TestsHelper;

/**
 * Tests {@link IsAuthenticatedAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class IsAuthenticatedAuthorizerTests extends AbstractIsAuthenticatedAuthorizerTests {

    protected Authorizer newAuthorizer() {
        return new IsAuthenticatedAuthorizer();
    }

    @Test
    public void testAnonymousProfileRedirectionUrl() {
        profiles.add(new AnonymousProfile());
        ((IsAuthenticatedAuthorizer) authorizer).setRedirectionUrl(PAC4J_URL);
        TestsHelper.expectException(() -> authorizer.isAuthorized(MockWebContext.create(), profiles), RequiresHttpAction.class, "user should be authenticated");
    }
}
