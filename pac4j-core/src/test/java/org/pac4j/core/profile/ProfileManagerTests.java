package org.pac4j.core.profile;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.util.Pac4jConstants;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

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

    private ProfileManager profileManager;

    private LinkedHashMap<String, CommonProfile> profiles;

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
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.get(true).get());
        assertTrue(profileManager.isAuthenticated());
    }

    @Test
    public void testGetOneExpiredProfileFromSession() {
        profile1 = mock(CommonProfile.class);
        when(profile1.getId()).thenReturn(ID1);
        when(profile1.getClientName()).thenReturn(CLIENT1);
        when(profile1.isExpired()).thenReturn(true);
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertFalse(profileManager.get(true).isPresent());
        assertFalse(profileManager.isAuthenticated());
        final LinkedHashMap<String, UserProfile> profiles =
            (LinkedHashMap<String, UserProfile>) context.getSessionStore().get(context, Pac4jConstants.USER_PROFILES).get();
        assertEquals(0, profiles.size());
    }

    @Test
    public void testGetOneRenewedProfileFromSession() {
        profile1 = mock(CommonProfile.class);
        when(profile1.getId()).thenReturn(ID1);
        when(profile1.getClientName()).thenReturn(CLIENT1);
        when(profile1.isExpired()).thenReturn(true);
        profiles.put(CLIENT1, profile1);
        final BaseClient client1 = mock(BaseClient.class);
        when(client1.getName()).thenReturn(CLIENT1);
        profileManager.setConfig(new Config(client1));
        when(client1.renewUserProfile(profile1, context)).thenReturn(Optional.of(profile2));
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile2, profileManager.get(true).get());
        assertTrue(profileManager.isAuthenticated());
        final LinkedHashMap<String, UserProfile> profiles =
            (LinkedHashMap<String, UserProfile>) context.getSessionStore().get(context, Pac4jConstants.USER_PROFILES).get();
        assertEquals(profile2, profiles.get(CLIENT1));
    }

    @Test
    public void testGetOneRenewedProfileFromSessionButNoConfig() {
        profile1 = mock(CommonProfile.class);
        when(profile1.getId()).thenReturn(ID1);
        when(profile1.getClientName()).thenReturn(CLIENT1);
        when(profile1.isExpired()).thenReturn(true);
        profiles.put(CLIENT1, profile1);
        final BaseClient client1 = mock(BaseClient.class);
        when(client1.getName()).thenReturn(CLIENT1);
        when(client1.renewUserProfile(profile1, context)).thenReturn(Optional.of(profile2));
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertFalse(profileManager.get(true).isPresent());
        assertFalse(profileManager.isAuthenticated());
        final LinkedHashMap<String, UserProfile> profiles =
            (LinkedHashMap<String, UserProfile>) context.getSessionStore().get(context, Pac4jConstants.USER_PROFILES).get();
        assertEquals(0, profiles.size());
    }

    @Test
    public void testGetOneRenewedProfileFromSessionButNoRelatedClient() {
        profile1 = mock(CommonProfile.class);
        when(profile1.getId()).thenReturn(ID1);
        when(profile1.getClientName()).thenReturn(CLIENT1);
        when(profile1.isExpired()).thenReturn(true);
        profiles.put(CLIENT1, profile1);
        final BaseClient client1 = mock(BaseClient.class);
        when(client1.getName()).thenReturn(CLIENT2);
        profileManager.setConfig(new Config(client1));
        when(client1.renewUserProfile(profile1, context)).thenReturn(Optional.of(profile2));
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertFalse(profileManager.get(true).isPresent());
        assertFalse(profileManager.isAuthenticated());
        final LinkedHashMap<String, UserProfile> profiles =
            (LinkedHashMap<String, UserProfile>) context.getSessionStore().get(context, Pac4jConstants.USER_PROFILES).get();
        assertEquals(0, profiles.size());
    }

    @Test
    public void testGetOneProfilesFromSessionFirstOneAnonymous() {
        profiles.put("first", new AnonymousProfile());
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.get(true).get());
    }

    @Test
    public void testGetLikeProfileInSessionNoHintDefaultSession() {
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.getLikeDefaultSecurityLogic(true).get());
    }

    @Test
    public void testGetLikeProfileInSessionRequestHintDefaultSession() {
        context.setRequestAttribute(Pac4jConstants.LOAD_PROFILES_FROM_SESSION, false);
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(false, profileManager.getLikeDefaultSecurityLogic(true).isPresent());
    }

    @Test
    public void testGetLikeProfileInSessionSessionHintDefaultRequest() {
        context.setRequestAttribute(Pac4jConstants.LOAD_PROFILES_FROM_SESSION, true);
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.getLikeDefaultSecurityLogic(false).get());
    }

    @Test
    public void testGetAllLikeProfileInSessionNoHintDefaultSession() {
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.getAllLikeDefaultSecurityLogic(true).get(0));
    }

    @Test
    public void testGetAllLikeProfileInSessionRequestHintDefaultSession() {
        context.setRequestAttribute(Pac4jConstants.LOAD_PROFILES_FROM_SESSION, false);
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(0, profileManager.getAllLikeDefaultSecurityLogic(true).size());
    }

    @Test
    public void testGetAllLikeProfileInSessionSessionHintDefaultRequest() {
        context.setRequestAttribute(Pac4jConstants.LOAD_PROFILES_FROM_SESSION, true);
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.getAllLikeDefaultSecurityLogic(false).get(0));
    }

    @Test
    public void testGetOneTwoProfilesFromSession() {
        profiles.put(CLIENT1, profile1);
        profiles.put(CLIENT2, profile2);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.get(true).get());
        assertTrue(profileManager.isAuthenticated());
    }

    @Test
    public void testGetOneProfileFromRequest() {
        profiles.put(CLIENT1, profile1);
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
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.getAll(true).get(0));
    }

    @Test
    public void testGetAllTwoProfilesFromSession() {
        profiles.put(CLIENT1, profile1);
        profiles.put(CLIENT2, profile2);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(profile1, profileManager.getAll(true).get(0));
        assertEquals(profile2, profileManager.getAll(true).get(1));
    }

    @Test
    public void testGetAllTwoProfilesFromSessionAndRequest() {
        profiles.put(CLIENT1, profile1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        final LinkedHashMap<String, CommonProfile> profiles2 = new LinkedHashMap<>();
        profiles2.put(CLIENT2, profile2);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles2);
        assertEquals(profile1, profileManager.getAll(true).get(0));
        assertEquals(profile2, profileManager.getAll(true).get(1));
    }

    @Test
    public void testGetAllOneProfileFromRequest() {
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        assertEquals(0, profileManager.getAll(false).size());
    }

    @Test
    public void testRemoveSessionFalse() {
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        profileManager.remove(false);
        assertTrue(profileManager.get(true).isPresent());
    }

    @Test
    public void testRemoveSessionTrue() {
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        profileManager.remove(true);
        assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void testLogoutSession() {
        profiles.put(CLIENT1, profile1);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        profileManager.logout();
        assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void testRemoveRequestFalse() {
        profiles.put(CLIENT1, profile1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.remove(false);
        assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void testRemoveRequestTrue() {
        profiles.put(CLIENT1, profile1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.remove(true);
        assertFalse(profileManager.get(true).isPresent());
    }

    @Test
    public void saveOneProfileNoMulti() {
        profiles.put(CLIENT1, profile1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.save(true, profile2, false);
        final List<CommonProfile> profiles = profileManager.getAll(true);
        assertEquals(1, profiles.size());
        assertEquals(profile2, profiles.get(0));
    }

    @Test
    public void saveTwoProfilesNoMulti() {
        profiles.put(CLIENT1, profile1);
        profiles.put(CLIENT2, profile2);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.save(true, profile3, false);
        final List<CommonProfile> profiles = profileManager.getAll(true);
        assertEquals(1, profiles.size());
        assertEquals(profile3, profiles.get(0));
    }

    @Test
    public void saveOneProfileMulti() {
        profiles.put(CLIENT1, profile1);
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        profileManager.save(true, profile2, true);
        final List<CommonProfile> profiles = profileManager.getAll(true);
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
        final List<CommonProfile> profiles = profileManager.getAll(true);
        assertEquals(2, profiles.size());
        assertEquals(profile2, profiles.get(0));
        assertEquals(profile3, profiles.get(1));
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
