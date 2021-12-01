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
 * Tests {@link RequireAnyPermissionAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class RequireAnyPermissionAuthorizerTests {

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
    public void testHasAnyPermissionOnePermission() {
        final var authorizer = new RequireAnyPermissionAuthorizer(PERMISSION1);
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermission2() {
        final var authorizer = new RequireAnyPermissionAuthorizer();
        authorizer.setElements(PERMISSION1);
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermission3() {
        final var authorizer = new RequireAnyPermissionAuthorizer();
        authorizer.setElements(Arrays.asList(PERMISSION1));
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermission4() {
        final var authorizer = new RequireAnyPermissionAuthorizer();
        authorizer.setElements(new HashSet<>(Arrays.asList(PERMISSION1)));
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermissionFail() {
        final var authorizer = new RequireAnyPermissionAuthorizer(new String[] {PERMISSION1});
        profile.addPermission(PERMISSION2);
        assertFalse(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyPermissionOnePermissionTwoProfiles() {
        final var authorizer = new RequireAnyPermissionAuthorizer(new String[] {PERMISSION1});
        profile.addPermission(PERMISSION2);
        final var profile2 = new CommonProfile();
        profile2.addPermission(PERMISSION1);
        profiles.add(profile2);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyPermissionNull() {
        final var authorizer = new RequireAnyPermissionAuthorizer((List<String>) null);
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyPermissionEmpty() {
        final var authorizer = new RequireAnyPermissionAuthorizer(new String[] {});
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyPermissionOkTwoPermissions() {
        final var authorizer = new RequireAnyPermissionAuthorizer(PERMISSION2, PERMISSION1);
        profile.addPermission(PERMISSION1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyPermissionProfileTwoPermissionsFail() {
        final var authorizer = new RequireAnyPermissionAuthorizer(new String[] {PERMISSION2});
        profile.addPermission(PERMISSION1);
        profile.addPermission(PERMISSION3);
        assertFalse(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }
}
