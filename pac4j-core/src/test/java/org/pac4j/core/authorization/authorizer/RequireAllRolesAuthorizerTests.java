package org.pac4j.core.authorization.authorizer;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

import java.util.ArrayList;
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

    private List<UserProfile> profiles;

    private UserProfile profile;

    @Before
    public void setUp() {
        profile = new CommonProfile();
        profiles = new ArrayList<>();
        profiles.add(profile);
    }

    @Test
    public void testHasAllRolesOkDifferentOrder() throws RequiresHttpAction {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(ROLE3, ROLE1);
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder2() throws RequiresHttpAction {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(Arrays.asList(ROLE3, ROLE1));
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder3() throws RequiresHttpAction {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer();
        authorizer.setElements(ROLE3, ROLE1);
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder4() throws RequiresHttpAction {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer();
        authorizer.setElements(new HashSet<>(Arrays.asList(ROLE3, ROLE1)));
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesOkDifferentOrder5() throws RequiresHttpAction {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer();
        authorizer.setElements(Arrays.asList(ROLE3, ROLE1));
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesNull() throws RequiresHttpAction {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer((List<String>) null);
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesEmpty() throws RequiresHttpAction {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(new String[]{});
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesTwoRolesFail() throws RequiresHttpAction {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(new String[]{ROLE3, ROLE1});
        profile.addRole(ROLE1);
        profile.addRole(ROLE2);
        assertFalse(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllRolesTwoRolesFailTwoProfiles() throws RequiresHttpAction {
        final RequireAllRolesAuthorizer authorizer = new RequireAllRolesAuthorizer(new String[]{ROLE3, ROLE1});
        profile.addRole(ROLE1);
        profile.addRole(ROLE2);
        final UserProfile profile2 = new CommonProfile();
        profile2.addRole(ROLE3);
        profiles.add(profile2);
        assertFalse(authorizer.isAuthorized(context, profiles));
    }
}
