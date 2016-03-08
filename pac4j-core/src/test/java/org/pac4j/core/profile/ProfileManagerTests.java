package org.pac4j.core.profile;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;

import java.util.LinkedHashMap;

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
    private final static UserProfile PROFILE1 = new CommonProfile();
    private final static UserProfile PROFILE2 = new CommonProfile();
    private final static UserProfile PROFILE3 = new CommonProfile();

    private MockWebContext context;

    private ProfileManager profileManager;

    private LinkedHashMap<String, UserProfile> profiles;

    static {
        PROFILE1.setId("ID1");
        PROFILE2.setId("ID2");
        PROFILE3.setId("ID3");
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
        context.setSessionAttribute(Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(PROFILE1, profileManager.get(true).get());
    }

    @Test
    public void testGetTwoProfilesFromSession() {
        profiles.put(CLIENT1, PROFILE1);
        profiles.put(CLIENT2, PROFILE2);
        context.setSessionAttribute(Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(PROFILE1, profileManager.get(true).get());
    }

    @Test
    public void testGetOneProfileFromRequest() {
        profiles.put(CLIENT1, PROFILE1);
        context.setSessionAttribute(Pac4jConstants.USER_PROFILES, profiles);
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
    }

    @Test
    public void testGetAllOneProfileFromSession() {
        profiles.put(CLIENT1, PROFILE1);
        context.setSessionAttribute(Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(PROFILE1, profileManager.getAll(true).get(0));
    }

    @Test
    public void testGetAllTwoProfilesFromSession() {
        profiles.put(CLIENT1, PROFILE1);
        profiles.put(CLIENT2, PROFILE2);
        context.setSessionAttribute(Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(PROFILE1, profileManager.getAll(true).get(0));
        assertEquals(PROFILE2, profileManager.getAll(true).get(1));
    }

    @Test
    public void testGetAllOneProfileFromRequest() {
        profiles.put(CLIENT1, PROFILE1);
        context.setSessionAttribute(Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(0, profileManager.getAll(false).size());
    }
}
