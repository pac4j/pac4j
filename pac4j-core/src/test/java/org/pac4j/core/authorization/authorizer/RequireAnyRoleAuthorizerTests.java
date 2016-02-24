package org.pac4j.core.authorization.authorizer;

import org.junit.Test;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Tests {@link RequireAnyRoleAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class RequireAnyRoleAuthorizerTests {

    private static final String ROLE1 = "role1";
    private static final String ROLE2 = "role2";
    private static final String ROLE3 = "role3";

    private final J2EContext context = new J2EContext(null, null);

    @Test
    public void testHasAnyRoleOneRole() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(ROLE1);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyRoleOneRole2() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(ROLE1);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyRoleOneRole3() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(Arrays.asList(ROLE1));
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyRoleOneRole4() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(new HashSet<String>(Arrays.asList(ROLE1)));
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyRoleOneRoleFail() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(new String[] { ROLE1 });
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE2);
        assertFalse(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyRoleNull() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer((List<String>) null);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyRoleEmpty() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(new String[] {});
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyRoleOkTwoRoles() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(ROLE2, ROLE1);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAnyRoleProfileTwoRolesFail() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(new String[] { ROLE2 });
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertFalse(authorizer.isAuthorized(context, profile));
    }
}
