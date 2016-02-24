package org.pac4j.core.authorization.authorizer;

import org.junit.Test;
import org.pac4j.core.authorization.authorizer.RequireAllRolesAuthorizer;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link RequireAllRolesAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class RequireAllRolesAuthorizerTests {

    private static final String ROLE1 = "role1";
    private static final String ROLE2 = "role2";
    private static final String ROLE3 = "role3";

    private final J2EContext context = new J2EContext(null, null);

    @Test
    public void testHasAllRolesOkDifferentOrder() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(ROLE3, ROLE1);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder2() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(Arrays.asList(ROLE3, ROLE1));
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder3() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer();
        authorizer.setElements(ROLE3, ROLE1);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder4() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer();
        authorizer.setElements(new HashSet<String>(Arrays.asList(ROLE3, ROLE1)));
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder5() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer();
        authorizer.setElements(Arrays.asList(ROLE3, ROLE1));
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesNull() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer((List<String>) null);
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesEmpty() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(new String[]{});
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllRolesTwoRolesFail() {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(new String[]{ROLE3, ROLE1});
        final CommonProfile profile = new CommonProfile();
        profile.addRole(ROLE1);
        profile.addRole(ROLE2);
        assertFalse(authorizer.isAuthorized(context, profile));
    }
}
