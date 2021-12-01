package org.pac4j.core.authorization.authorizer;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

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

    private final MockWebContext context = MockWebContext.create();

    private List<UserProfile> profiles;

    private CommonProfile profile;

    @Before
    public void setUp() {
        profile = new CommonProfile();
        profiles = new ArrayList<>();
        profiles.add(profile);
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder() {
        final var authorizer = new RequireAllPermissionsAuthorizer(PERMISSION3, PERMISSION1);
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrderTwoProfiles() {
        final var authorizer = new RequireAllPermissionsAuthorizer(PERMISSION3, PERMISSION1);
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        profiles.add(new CommonProfile());
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder2() {
        final var authorizer = new RequireAllPermissionsAuthorizer(Arrays.asList(PERMISSION3, PERMISSION1));
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder3() {
        final var authorizer = new RequireAllPermissionsAuthorizer();
        authorizer.setElements(PERMISSION3, PERMISSION1);
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder4() {
        final var authorizer = new RequireAllPermissionsAuthorizer();
        authorizer.setElements(new HashSet<>(Arrays.asList(PERMISSION3, PERMISSION1)));
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAllPermissionsOkDifferentOrder5() {
        final var authorizer = new RequireAllPermissionsAuthorizer();
        authorizer.setElements(Arrays.asList(PERMISSION3, PERMISSION1));
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAllPermissionsNull() {
        final var authorizer = new RequireAllPermissionsAuthorizer((List<String>) null);
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAllPermissionsEmpty() {
        final var authorizer = new RequireAllPermissionsAuthorizer(new String[]{});
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAllPermissionsTwoPermissionsFail() {
        final var authorizer = new RequireAllPermissionsAuthorizer(new String[]{PERMISSION3, PERMISSION1});
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION2);
        assertFalse(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }
}
