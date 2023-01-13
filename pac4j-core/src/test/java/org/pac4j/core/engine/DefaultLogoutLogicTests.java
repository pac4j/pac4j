package org.pac4j.core.engine;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.adapter.FrameworkAdapter;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.LinkedHashMap;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link DefaultLogoutLogic}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class DefaultLogoutLogicTests implements TestsConstants {

    private LogoutLogic logic;

    private MockWebContext context;

    private SessionStore sessionStore;

    private Config config;

    private String defaultUrl;

    private String logoutUrlPattern;

    private Boolean localLogout;

    private Boolean centralLogout;

    private LinkedHashMap<String, CommonProfile> profiles;

    private HttpAction action;

    @Before
    public void setUp() {
        logic = new DefaultLogoutLogic();
        config = new Config();
        context = MockWebContext.create();
        config.setWebContextFactory(p -> context);
        sessionStore = new MockSessionStore();
        config.setSessionStoreFactory(p -> sessionStore);
        config.setClients(new Clients());
        config.setHttpActionAdapter((act, ctx) -> { action = act; return null; });
        defaultUrl = null;
        logoutUrlPattern = null;
        localLogout = null;
        centralLogout = null;
        profiles = new LinkedHashMap<>();
    }

    private void call() {
        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);

        logic.perform(config, defaultUrl, logoutUrlPattern, localLogout, null, centralLogout, mock(FrameworkParameters.class));
    }

    @Test
    public void testNullConfig() {
        config = null;
        TestsHelper.expectException(this::call, TechnicalException.class, "config cannot be null");
    }

    @Test
    public void testNullContext() {
        context = null;
        TestsHelper.expectException(this::call, TechnicalException.class, "context cannot be null");
    }

    @Test
    public void testNullHttpActionAdapter() {
        config.setHttpActionAdapter(null);
        TestsHelper.expectException(this::call, TechnicalException.class, "httpActionAdapter cannot be null");
    }

    @Test
    public void testBlankLogoutUrlPattern() {
        logoutUrlPattern = Pac4jConstants.EMPTY_STRING;
        TestsHelper.expectException(this::call, TechnicalException.class, "logoutUrlPattern cannot be blank");
    }

    private void addProfilesToContext() {
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
    }

    private LinkedHashMap<String, CommonProfile> getProfilesFromRequest() {
        return (LinkedHashMap<String, CommonProfile>) context.getRequestAttribute(Pac4jConstants.USER_PROFILES).get();
    }

    private LinkedHashMap<String, CommonProfile> getProfilesFromSession() {
        return (LinkedHashMap<String, CommonProfile>) sessionStore.get(context, Pac4jConstants.USER_PROFILES).get();
    }

    private void expectedNProfiles(final int n) {
        assertEquals(n, getProfilesFromRequest().size());
        assertEquals(n, getProfilesFromSession().size());
    }

    @Test
    public void testLogoutPerformed() {
        profiles.put(NAME, new CommonProfile());
        addProfilesToContext();
        call();
        assertEquals(204, action.getCode());
        expectedNProfiles(0);
    }

    @Test
    public void testLogoutNotPerformedBecauseLocalLogoutIsFalse() {
        profiles.put(NAME, new CommonProfile());
        addProfilesToContext();
        localLogout = false;
        call();
        assertEquals(204, action.getCode());
        expectedNProfiles(1);
    }

    @Test
    public void testLogoutPerformedBecauseLocalLogoutIsFalseButMultipleProfiles() {
        profiles.put(NAME, new CommonProfile());
        profiles.put(VALUE, new CommonProfile());
        addProfilesToContext();
        localLogout = false;
        call();
        assertEquals(204, action.getCode());
        expectedNProfiles(0);
    }

    @Test
    public void testCentralLogout() {
        val profile = new CommonProfile();
        profile.setClientName(NAME);
        val client = new MockIndirectClient(NAME);
        client.setCallbackUrl(PAC4J_BASE_URL);
        client.setLogoutActionBuilder((ctx, store, pmf, p, targetUrl) -> Optional.of(new FoundAction(CALLBACK_URL + "?p=" + targetUrl)));
        config.setClients(new Clients(client));
        profiles.put(NAME, profile);
        addProfilesToContext();
        centralLogout = true;
        context.addRequestParameter(Pac4jConstants.URL, CALLBACK_URL);
        logoutUrlPattern = ".*";
        call();
        assertEquals(302, action.getCode());
        assertEquals(CALLBACK_URL + "?p=" + CALLBACK_URL, ((FoundAction) action).getLocation());
        expectedNProfiles(0);
    }

    @Test
    public void testCentralLogoutWithRelativeUrl() {
        val profile = new CommonProfile();
        profile.setClientName(NAME);
        val client = new MockIndirectClient(NAME);
        client.setCallbackUrl(PAC4J_BASE_URL);
        client.setLogoutActionBuilder((ctx, store, pmf, p, targetUrl) -> Optional.of(new FoundAction(CALLBACK_URL + "?p=" + targetUrl)));
        config.setClients(new Clients(client));
        profiles.put(NAME, profile);
        addProfilesToContext();
        centralLogout = true;
        context.addRequestParameter(Pac4jConstants.URL, PATH);
        call();
        assertEquals(302, action.getCode());
        assertEquals(CALLBACK_URL + "?p=null", ((FoundAction) action).getLocation());
        expectedNProfiles(0);
    }

    @Test
    public void testLogoutWithDefaultUrl() {
        defaultUrl = CALLBACK_URL;
        call();
        assertEquals(302, action.getCode());
        assertEquals(CALLBACK_URL, ((FoundAction) action).getLocation());
    }

    @Test
    public void testLogoutWithGoodUrl() {
        context.addRequestParameter(Pac4jConstants.URL, PATH);
        call();
        assertEquals(302, action.getCode());
        assertEquals(PATH, ((FoundAction) action).getLocation());
    }

    @Test
    public void testLogoutWithSlashUrl() {
        context.addRequestParameter(Pac4jConstants.URL, "/");
        call();
        assertEquals(302, action.getCode());
        assertEquals("/", ((FoundAction) action).getLocation());
    }

    @Test
    public void testLogoutWithDoubleSlashUrlNoDefaultUrl() {
        context.addRequestParameter(Pac4jConstants.URL, "//evil.example.org/download.exe");
        call();
        assertEquals(204, action.getCode());
        assertEquals(Pac4jConstants.EMPTY_STRING, context.getResponseContent());
    }

    @Test
    public void testLogoutWithDoubleSlashUrlDefaultUrl() {
        context.addRequestParameter(Pac4jConstants.URL, "//evil.example.org/download.exe");
        defaultUrl = CALLBACK_URL;
        call();
        assertEquals(302, action.getCode());
        assertEquals(CALLBACK_URL, ((FoundAction) action).getLocation());
    }

    @Test
    public void testLogoutWithBadUrlNoDefaultUrl() {
        context.addRequestParameter(Pac4jConstants.URL, PATH);
        logoutUrlPattern = VALUE;
        call();
        assertEquals(204, action.getCode());
        assertEquals(Pac4jConstants.EMPTY_STRING, context.getResponseContent());
    }

    @Test
    public void testLogoutWithBadUrlButDefaultUrl() {
        context.addRequestParameter(Pac4jConstants.URL, PATH);
        defaultUrl = CALLBACK_URL;
        logoutUrlPattern = VALUE;
        call();
        assertEquals(302, action.getCode());
        assertEquals(CALLBACK_URL, ((FoundAction) action).getLocation());
    }
}
