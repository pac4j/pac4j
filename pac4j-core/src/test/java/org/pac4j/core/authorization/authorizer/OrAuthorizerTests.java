package org.pac4j.core.authorization.authorizer;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.pac4j.core.authorization.authorizer.OrAuthorizer.or;
import static org.pac4j.core.authorization.authorizer.RequireAnyPermissionAuthorizer.requireAnyPermission;
import static org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer.requireAnyRole;

/**
 * Tests {@link OrAuthorizer}
 *
 * @author Sergey Morgunov
 * @since 3.4.0
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class OrAuthorizerTests {

    private List<CommonProfile> profiles = new ArrayList<>();

    @Before
    public void setUp() {
        CommonProfile profile = new CommonProfile();
        profile.addRole("profile_role");
        profile.addPermission("profile_permission");
        profiles.add(profile);
    }

    @Test
    public void testDisjunctionAuthorizer1() {
        final Authorizer<CommonProfile> authorizer = or(
            requireAnyRole("profile_role2"),
            requireAnyPermission("profile_permission2")
        );
        assertFalse(authorizer.isAuthorized(MockWebContext.create(), profiles));
    }

    @Test
    public void testDisjunctionAuthorizer2() {
        final Authorizer<CommonProfile> authorizer = or(
            requireAnyRole("profile_role2"),
            requireAnyPermission("profile_permission")
        );
        assertTrue(authorizer.isAuthorized(MockWebContext.create(), profiles));
    }

    @Test
    public void testDisjunctionAuthorizer3() {
        final Authorizer<CommonProfile> authorizer = or(
            requireAnyRole("profile_role"),
            requireAnyPermission("profile_permission2")
        );
        assertTrue(authorizer.isAuthorized(MockWebContext.create(), profiles));
    }
}
