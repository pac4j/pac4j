package org.pac4j.core.authorization.authorizer;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import static org.pac4j.core.context.HttpConstants.*;

/**
 * Tests {@link CheckHttpMethodAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class CheckHttpMethodAuthorizerTests {

    private List<UserProfile> profiles;

    @Before
    public void setUp() {
        profiles = new ArrayList<>();
        profiles.add(new CommonProfile());
    }

    @Test
    public void testGoodHttpMethod() {
        final CheckHttpMethodAuthorizer authorizer = new CheckHttpMethodAuthorizer(HTTP_METHOD.GET, HTTP_METHOD.POST);
        assertTrue(authorizer.isAuthorized(MockWebContext.create().setRequestMethod(HTTP_METHOD.GET.name()), profiles));
    }

    @Test
    public void testBadHttpMethod() {
        final CheckHttpMethodAuthorizer authorizer = new CheckHttpMethodAuthorizer(HTTP_METHOD.PUT);
        assertFalse(authorizer.isAuthorized(MockWebContext.create().setRequestMethod(HTTP_METHOD.DELETE.name()), profiles));
    }
}
