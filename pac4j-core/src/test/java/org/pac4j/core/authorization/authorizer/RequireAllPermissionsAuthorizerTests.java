package org.pac4j.core.authorization.authorizer;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.CommonProfile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

/**
 * Tests {@link RequireAllPermissionsAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class RequireAllPermissionsAuthorizerTests {

    private static final String PERMISSION1 = "permission1";
    private static final String PERMISSION2 = "permission2";
    private static final String PERMISSION3 = "permission3";

    private final JEEContext context = new JEEContext(mock(HttpServletRequest.class), mock(HttpServletResponse.class));

    private List<CommonProfile> profiles;

    private CommonProfile profile;

    @Before
    public void setUp() {
        profile = new CommonProfile();
        profiles = new ArrayList<>();
        profiles.add(profile);
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(PERMISSION3, PERMISSION1);
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrderTwoProfiles() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(PERMISSION3, PERMISSION1);
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        profiles.add(new CommonProfile());
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder2() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(Arrays.asList(PERMISSION3, PERMISSION1));
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder3() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer();
        authorizer.setElements(PERMISSION3, PERMISSION1);
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder4() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer();
        authorizer.setElements(new HashSet<>(Arrays.asList(PERMISSION3, PERMISSION1)));
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder5() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer();
        authorizer.setElements(Arrays.asList(PERMISSION3, PERMISSION1));
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsNull() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer((List<String>) null);
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsEmpty() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(new String[]{});
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testHasAllPermissionsTwoPermissionsFail() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(new String[]{PERMISSION3, PERMISSION1});
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION2);
        assertFalse(authorizer.isAuthorized(context, profiles));
    }
}
