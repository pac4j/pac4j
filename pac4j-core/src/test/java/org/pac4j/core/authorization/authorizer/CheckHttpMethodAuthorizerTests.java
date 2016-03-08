package org.pac4j.core.authorization.authorizer;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.profile.CommonProfile;

import static org.junit.Assert.*;

import static org.pac4j.core.context.HttpConstants.*;

/**
 * Tests {@link CheckHttpMethodAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class CheckHttpMethodAuthorizerTests {

    @Test
    public void testGoodHttpMethod() {
        final CheckHttpMethodAuthorizer authorizer = new CheckHttpMethodAuthorizer(HTTP_METHOD.GET, HTTP_METHOD.POST);
        assertTrue(authorizer.isAuthorized(MockWebContext.create().setRequestMethod("GET"), new CommonProfile()));
    }

    @Test
    public void testBadHttpMethod() {
        final CheckHttpMethodAuthorizer authorizer = new CheckHttpMethodAuthorizer(HTTP_METHOD.PUT);
        assertFalse(authorizer.isAuthorized(MockWebContext.create().setRequestMethod("DELETE"), new CommonProfile()));
    }
}
