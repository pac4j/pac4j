package org.pac4j.core.authorization.authorizer;

import org.junit.Test;
import org.pac4j.core.authorization.authorizer.RequireAllPermissionsAuthorizer;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    private final J2EContext context = new J2EContext(null, null);

    @Test
    public void testHasAllPermissionsOkDifferentOrder() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(PERMISSION3, PERMISSION1);
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder2() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(Arrays.asList(PERMISSION3, PERMISSION1));
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder3() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer();
        authorizer.setElements(PERMISSION3, PERMISSION1);
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder4() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer();
        authorizer.setElements(new HashSet<String>(Arrays.asList(PERMISSION3, PERMISSION1)));
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder5() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer();
        authorizer.setElements(Arrays.asList(PERMISSION3, PERMISSION1));
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllPermissionsNull() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer((List<String>) null);
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllPermissionsEmpty() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(new String[]{});
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, profile));
    }

    @Test
    public void testHasAllPermissionsTwoPermissionsFail() {
        final RequireAllPermissionsAuthorizer authorizer = new RequireAllPermissionsAuthorizer(new String[]{PERMISSION3, PERMISSION1});
        final CommonProfile profile = new CommonProfile();
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION2);
        assertFalse(authorizer.isAuthorized(context, profile));
    }
}
