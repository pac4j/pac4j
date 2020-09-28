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
 * Tests {@link RequireAnyPermissionAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class RequireAnyPermissionAuthorizerTests {

    private static final String PERMISSION1 = "permission1";
    private static final String PERMISSION2 = "permission2";
    private static final String PERMISSION3 = "permission3";

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
    public void testHasAnyPermissionOnePermission() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(PERMISSION1);
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermission2() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer();
        authorizer.setElements(PERMISSION1);
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermission3() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer();
        authorizer.setElements(Arrays.asList(PERMISSION1));
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermission4() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer();
        authorizer.setElements(new HashSet<>(Arrays.asList(PERMISSION1)));
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermissionFail() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(new String[] {PERMISSION1});
        profile.addPermission(PERMISSION2);
        assertFalse(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermissionTwoProfiles() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(new String[] {PERMISSION1});
        profile.addPermission(PERMISSION2);
        final CommonProfile profile2 = new CommonProfile();
        profile2.addPermission(PERMISSION1);
        profiles.add(profile2);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionNull() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer((List<String>) null);
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionEmpty() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(new String[] {});
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionOkTwoPermissions() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(PERMISSION2, PERMISSION1);
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAnyPermissionProfileTwoPermissionsFail() {
        final RequireAnyPermissionAuthorizer authorizer = new RequireAnyPermissionAuthorizer(new String[] {PERMISSION2});
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertFalse(authorizer.isAuthorized(context, profiles));
    }
}
