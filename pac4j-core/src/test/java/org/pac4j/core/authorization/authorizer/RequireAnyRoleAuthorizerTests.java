package org.pac4j.core.authorization.authorizer;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

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

    private final JEEContext context = new JEEContext(mock(HttpServletRequest.class), mock(HttpServletResponse.class));

    private List<UserProfile> profiles;

    private CommonProfile profile;

    @Before
    public void setUp() {
        profile = new CommonProfile();
        profiles = new ArrayList<>();
        profiles.add(profile);
    }

    @Test
    public void testHasAnyRoleOneRole() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(ROLE1);
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleOneRole2() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(ROLE1);
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleOneRoleTwoProfiles() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(ROLE1);
        profile.addRole(ROLE1);
        profiles.add(new CommonProfile());
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleOneRole3() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(Arrays.asList(ROLE1));
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleOneRole4() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(new HashSet<String>(Arrays.asList(ROLE1)));
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleOneRoleFail() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(new String[] {ROLE1});
        profile.addRole(ROLE2);
        assertFalse(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleNull() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer((List<String>) null);
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleEmpty() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(new String[] {});
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleOkTwoRoles() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(ROLE2, ROLE1);
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyRoleProfileTwoRolesFail() {
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer(new String[] {ROLE2});
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertFalse(authorizer.isAuthorized(context, profiles));
    }
}
