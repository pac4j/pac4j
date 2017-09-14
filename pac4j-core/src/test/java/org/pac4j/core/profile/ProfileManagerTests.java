package org.pac4j.core.profile;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests {@link ProfileManager}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class ProfileManagerTests {

    private final static String CLIENT1 = "client1";
    private final static String CLIENT2 = "client2";
    private final static CommonProfile PROFILE1 = new CommonProfile();
    private final static CommonProfile PROFILE2 = new CommonProfile();
    private final static CommonProfile PROFILE3 = new CommonProfile();

    private MockWebContext context;

    private ProfileManager profileManager;

    private LinkedHashMap<String, CommonProfile> profiles;

    static {
        PROFILE1.setId("ID1");
        PROFILE1.setClientName(CLIENT1);
        PROFILE2.setId("ID2");
        PROFILE2.setClientName(CLIENT2);
        PROFILE3.setId("ID3");
        PROFILE3.setClientName(CLIENT1);
    }

    @Before
    public void setUp() {
        context = MockWebContext.create();
        profileManager = new ProfileManager(context);
        profiles = new LinkedHashMap<>();
    }

    @Test
    public void testGetNullProfile() {
        assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void testGetNoProfile() {
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void testGetOneProfileFromSession() {
        profiles.put(CLIENT1, PROFILE1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(PROFILE1, profileManager.get(true).get());
        assertTrue(profileManager.isAuthenticated());
    }

    @Test
    public void testGetOneProfilesFromSessionFirstOneAnonymous() {
        profiles.put("first", new AnonymousProfile());
        profiles.put(CLIENT1, PROFILE1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(PROFILE1, profileManager.get(true).get());
    }

    @Test
    public void testGetOneTwoProfilesFromSession() {
        profiles.put(CLIENT1, PROFILE1);
        profiles.put(CLIENT2, PROFILE2);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(PROFILE1, profileManager.get(true).get());
        assertTrue(profileManager.isAuthenticated());
    }

    @Test
    public void testGetOneProfileFromRequest() {
        profiles.put(CLIENT1, PROFILE1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertFalse(profileManager.get(false).isPresent());
    }

    @Test
    public void testGetAllNullProfile() {
        assertEquals(0, profileManager.getAll(true).size());
    }

    @Test
    public void testGetAllNoProfile() {
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(0, profileManager.getAll(true).size());
        assertFalse(profileManager.isAuthenticated());
    }

    @Test
    public void testGetAllOneProfileFromSession() {
        profiles.put(CLIENT1, PROFILE1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(PROFILE1, profileManager.getAll(true).get(0));
    }

    @Test
    public void testGetAllTwoProfilesFromSession() {
        profiles.put(CLIENT1, PROFILE1);
        profiles.put(CLIENT2, PROFILE2);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(PROFILE1, profileManager.getAll(true).get(0));
        assertEquals(PROFILE2, profileManager.getAll(true).get(1));
    }

    @Test
    public void testGetAllTwoProfilesFromSessionAndRequest() {
        profiles.put(CLIENT1, PROFILE1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        final LinkedHashMap<String, CommonProfile> profiles2 = new LinkedHashMap<>();
        profiles2.put(CLIENT2, PROFILE2);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles2);
        assertEquals(PROFILE1, profileManager.getAll(true).get(0));
        assertEquals(PROFILE2, profileManager.getAll(true).get(1));
    }

    @Test
    public void testGetAllOneProfileFromRequest() {
        profiles.put(CLIENT1, PROFILE1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(0, profileManager.getAll(false).size());
    }

    @Test
    public void testRemoveSessionFalse() {
        profiles.put(CLIENT1, PROFILE1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        profileManager.remove(false);
        assertTrue(profileManager.get(true).isPresent());
    }

    @Test
    public void testRemoveSessionTrue() {
        profiles.put(CLIENT1, PROFILE1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        profileManager.remove(true);
        assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void testLogoutSession() {
        profiles.put(CLIENT1, PROFILE1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        profileManager.logout();
        assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void testRemoveRequestFalse() {
        profiles.put(CLIENT1, PROFILE1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.remove(false);
        assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void testRemoveRequestTrue() {
        profiles.put(CLIENT1, PROFILE1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.remove(true);
        assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void saveOneProfileNoMulti() {
        profiles.put(CLIENT1, PROFILE1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.save(true, PROFILE2, false);
        final List<CommonProfile> profiles = profileManager.getAll(true);
        assertEquals(1, profiles.size());
        assertEquals(PROFILE2, profiles.get(0));
    }

    @Test
    public void saveTwoProfilesNoMulti() {
        profiles.put(CLIENT1, PROFILE1);
        profiles.put(CLIENT2, PROFILE2);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.save(true, PROFILE3, false);
        final List<CommonProfile> profiles = profileManager.getAll(true);
        assertEquals(1, profiles.size());
        assertEquals(PROFILE3, profiles.get(0));
    }

    @Test
    public void saveOneProfileMulti() {
        profiles.put(CLIENT1, PROFILE1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.save(true, PROFILE2, true);
        final List<CommonProfile> profiles = profileManager.getAll(true);
        assertEquals(2, profiles.size());
        assertEquals(PROFILE1, profiles.get(0));
        assertEquals(PROFILE2, profiles.get(1));
    }

    @Test
    public void saveTwoProfilesMulti() {
        profiles.put(CLIENT1, PROFILE1);
        profiles.put(CLIENT2, PROFILE2);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.save(true, PROFILE3, true);
        final List<CommonProfile> profiles = profileManager.getAll(true);
        assertEquals(2, profiles.size());
        assertEquals(PROFILE2, profiles.get(0));
        assertEquals(PROFILE3, profiles.get(1));
    }

    @Test
    public void testSingleProfileFromSessionDirectly() {
        final CommonProfile profile = new CommonProfile();
        profile.setClientName(CLIENT1);

        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profile);
        assertEquals(profile, profileManager.getAll(true).get(0));
    }

    @Test
    public void testSingleProfileFromRequestDirectly() {
        final CommonProfile profile = new CommonProfile();
        profile.setClientName(CLIENT1);

        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profile);
        assertEquals(profile, profileManager.getAll(false).get(0));
    }

    @Test
    public void testIsAuthenticatedAnonymousProfile() {
        profiles.put(CLIENT1, AnonymousProfile.INSTANCE);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(AnonymousProfile.INSTANCE, profileManager.getAll(true).get(0));
        assertFalse(profileManager.isAuthenticated());
    }
}
