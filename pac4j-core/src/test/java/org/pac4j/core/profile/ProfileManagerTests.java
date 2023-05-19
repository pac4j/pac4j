package org.pac4j.core.profile;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.Pac4jConstants;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link ProfileManager}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class ProfileManagerTests {

    private static final String ID1 = "ID1";
    private static final String ID2 = "ID2";
    private static final String ID3 = "ID3";
    private static final String CLIENT1 = "client1";
    private static final String CLIENT2 = "client2";
    private CommonProfile profile1;
    private CommonProfile profile2;
    private CommonProfile profile3;

    private MockWebContext context;

    private SessionStore sessionStore;

    private ProfileManager profileManager;

    private Map<String, CommonProfile> profiles;

    @Before
    public void setUp() {
        profile1 = new CommonProfile();
        profile1.setId(ID1);
        profile1.setClientName(CLIENT1);
        profile2 = new CommonProfile();
        profile2.setId(ID2);
        profile2.setClientName(CLIENT2);
        profile3 = new CommonProfile();
        profile3.setId(ID3);
        profile3.setClientName(CLIENT1);
        context = MockWebContext.create();
        sessionStore = new MockSessionStore();
        profileManager = new ProfileManager(context, sessionStore);
        profiles = new TreeMap<>();
    }

    @Test
    public void testGetNullProfile() {
        assertFalse(profileManager.getProfile().isPresent());
    }

    @Test
    public void testGetNoProfile() {
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        assertFalse(profileManager.getProfile().isPresent());
    }

    @Test
    public void testGetOneProfileFromSession() {
        profiles.put(CLIENT1, profile1);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.getProfile().get());
        assertTrue(profileManager.isAuthenticated());
    }

    @Test
    public void testGetOneExpiredProfileFromSession() {
        profile1 = mock(CommonProfile.class);
        when(profile1.getId()).thenReturn(ID1);
        when(profile1.getClientName()).thenReturn(CLIENT1);
        when(profile1.isExpired()).thenReturn(true);
        profiles.put(CLIENT1, profile1);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertFalse(profileManager.getProfile().isPresent());
        assertFalse(profileManager.isAuthenticated());
        Map<String, UserProfile> profiles =
            (LinkedHashMap<String, UserProfile>) sessionStore.get(context, Pac4jConstants.USER_PROFILES).get();
        assertEquals(0, profiles.size());
    }

    @Test
    public void testGetOneRenewedProfileFromSession() {
        profile1 = mock(CommonProfile.class);
        when(profile1.getId()).thenReturn(ID1);
        when(profile1.getClientName()).thenReturn(CLIENT1);
        when(profile1.isExpired()).thenReturn(true);
        profiles.put(CLIENT1, profile1);
        val client1 = mock(BaseClient.class);
        when(client1.getName()).thenReturn(CLIENT1);
        profileManager.setConfig(new Config(client1));
        when(client1.renewUserProfile(new CallContext(context, sessionStore), profile1)).thenReturn(Optional.of(profile2));
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile2, profileManager.getProfile().get());
        assertTrue(profileManager.isAuthenticated());
        Map<String, UserProfile> profiles =
            (LinkedHashMap<String, UserProfile>) sessionStore.get(context, Pac4jConstants.USER_PROFILES).get();
        assertEquals(profile2, profiles.get(CLIENT1));
    }

    @Test
    public void testGetOneRenewedProfileFromSessionButNoConfig() {
        profile1 = mock(CommonProfile.class);
        when(profile1.getId()).thenReturn(ID1);
        when(profile1.getClientName()).thenReturn(CLIENT1);
        when(profile1.isExpired()).thenReturn(true);
        profiles.put(CLIENT1, profile1);
        val client1 = mock(BaseClient.class);
        when(client1.getName()).thenReturn(CLIENT1);
        when(client1.renewUserProfile(new CallContext(context, sessionStore), profile1)).thenReturn(Optional.of(profile2));
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertFalse(profileManager.getProfile().isPresent());
        assertFalse(profileManager.isAuthenticated());
        Map<String, UserProfile> profiles =
            (LinkedHashMap<String, UserProfile>) sessionStore.get(context, Pac4jConstants.USER_PROFILES).get();
        assertEquals(0, profiles.size());
    }

    @Test
    public void testGetOneRenewedProfileFromSessionButNoRelatedClient() {
        profile1 = mock(CommonProfile.class);
        when(profile1.getId()).thenReturn(ID1);
        when(profile1.getClientName()).thenReturn(CLIENT1);
        when(profile1.isExpired()).thenReturn(true);
        profiles.put(CLIENT1, profile1);
        val client1 = mock(BaseClient.class);
        when(client1.getName()).thenReturn(CLIENT2);
        profileManager.setConfig(new Config(client1));
        when(client1.renewUserProfile(new CallContext(context, sessionStore), profile1)).thenReturn(Optional.of(profile2));
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertFalse(profileManager.getProfile().isPresent());
        assertFalse(profileManager.isAuthenticated());
        Map<String, UserProfile> profiles =
            (LinkedHashMap<String, UserProfile>) sessionStore.get(context, Pac4jConstants.USER_PROFILES).get();
        assertEquals(0, profiles.size());
    }

    @Test
    public void testGetOneProfilesFromSessionFirstOneAnonymous() {
        profiles.put("first", new AnonymousProfile());
        profiles.put(CLIENT1, profile1);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.getProfile().get());
    }


    @Test
    public void testGetOneTwoProfilesFromSession() {
        profiles.put(CLIENT1, profile1);
        profiles.put(CLIENT2, profile2);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.getProfile().get());
        assertTrue(profileManager.isAuthenticated());
    }

    @Test
    public void testGetAllNullProfile() {
        assertEquals(0, profileManager.getProfiles().size());
    }

    @Test
    public void testGetAllNoProfile() {
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(0, profileManager.getProfiles().size());
        assertFalse(profileManager.isAuthenticated());
    }

    @Test
    public void testGetAllOneProfileFromSession() {
        profiles.put(CLIENT1, profile1);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.getProfiles().get(0));
    }

    @Test
    public void testGetAllTwoProfilesFromSession() {
        profiles.put(CLIENT1, profile1);
        profiles.put(CLIENT2, profile2);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.getProfiles().get(0));
        assertEquals(profile2, profileManager.getProfiles().get(1));
    }

    @Test
    public void testGetAllTwoProfilesFromSessionAndRequest() {
        profiles.put(CLIENT1, profile1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        Map<String, CommonProfile> profiles2 = new LinkedHashMap<>();
        profiles2.put(CLIENT2, profile2);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles2);
        assertEquals(profile1, profileManager.getProfiles().get(0));
        assertEquals(profile2, profileManager.getProfiles().get(1));
    }

    @Test
    public void testRemoveSession() {
        profiles.put(CLIENT1, profile1);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        profileManager.removeProfiles();
        assertFalse(profileManager.getProfile().isPresent());
    }

    @Test
    public void testRemoveRequest() {
        profiles.put(CLIENT1, profile1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.removeProfiles();
        assertFalse(profileManager.getProfile().isPresent());
    }

    @Test
    public void saveOneProfileNoMulti() {
        profiles.put(CLIENT1, profile1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.save(true, profile2, false);
        val profiles = profileManager.getProfiles();
        assertEquals(1, profiles.size());
        assertEquals(profile2, profiles.get(0));
    }

    @Test
    public void saveTwoProfilesNoMulti() {
        profiles.put(CLIENT1, profile1);
        profiles.put(CLIENT2, profile2);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.save(true, profile3, false);
        val profiles = profileManager.getProfiles();
        assertEquals(1, profiles.size());
        assertEquals(profile3, profiles.get(0));
    }

    @Test
    public void saveOneProfileMulti() {
        profiles.put(CLIENT1, profile1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.save(true, profile2, true);
        val profiles = profileManager.getProfiles();
        assertEquals(2, profiles.size());
        assertEquals(profile1, profiles.get(0));
        assertEquals(profile2, profiles.get(1));
    }

    @Test
    public void saveTwoProfilesMulti() {
        profiles.put(CLIENT1, profile1);
        profiles.put(CLIENT2, profile2);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.save(true, profile3, true);
        val profiles = profileManager.getProfiles();
        assertEquals(2, profiles.size());
        assertEquals(profile2, profiles.get(0));
        assertEquals(profile3, profiles.get(1));
    }

    @Test
    public void testIsAuthenticatedAnonymousProfile() {
        profiles.put(CLIENT1, AnonymousProfile.INSTANCE);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(AnonymousProfile.INSTANCE, profileManager.getProfiles().get(0));
        assertFalse(profileManager.isAuthenticated());
    }
}
