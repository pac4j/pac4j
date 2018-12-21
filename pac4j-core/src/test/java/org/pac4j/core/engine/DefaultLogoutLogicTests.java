package org.pac4j.core.engine;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link DefaultLogoutLogic}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class DefaultLogoutLogicTests implements TestsConstants {

    private LogoutLogic<Object, WebContext> logic;

    private MockWebContext context;

    private Config config;

    private HttpActionAdapter<Object, WebContext> httpActionAdapter;

    private String defaultUrl;

    private String logoutUrlPattern;

    private Boolean localLogout;

    private Boolean centralLogout;

    private LinkedHashMap<String, CommonProfile> profiles;

    private HttpAction action;

    @Before
    public void setUp() {
        logic = new DefaultLogoutLogic<>();
        context = MockWebContext.create();
        config = new Config();
        config.setClients(new Clients());
        httpActionAdapter = (act, ctx) -> { action = act; return null; };
        defaultUrl = null;
        logoutUrlPattern = null;
        localLogout = null;
        centralLogout = null;
        profiles = new LinkedHashMap<>();
    }

    private void call() {
        logic.perform(context, config, httpActionAdapter, defaultUrl, logoutUrlPattern, localLogout, null, centralLogout);
    }

    @Test
    public void testNullConfig() {
        config = null;
        TestsHelper.expectException(() -> call(), TechnicalException.class, "config cannot be null");
    }

    @Test
    public void testNullContext() {
        context = null;
        TestsHelper.expectException(() -> call(), TechnicalException.class, "context cannot be null");
    }

    @Test
    public void testNullHttpActionAdapter() {
        httpActionAdapter = null;
        TestsHelper.expectException(() -> call(), TechnicalException.class, "httpActionAdapter cannot be null");
    }

    @Test
    public void testBlankLogoutUrlPattern() {
        logoutUrlPattern = "";
        TestsHelper.expectException(() -> call(), TechnicalException.class, "logoutUrlPattern cannot be blank");
    }

    private void addProfilesToContext() {
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
    }

    private LinkedHashMap<String, CommonProfile> getProfilesFromRequest() {
        return (LinkedHashMap<String, CommonProfile>) context.getRequestAttribute(Pac4jConstants.USER_PROFILES);
    }

    private LinkedHashMap<String, CommonProfile> getProfilesFromSession() {
        return (LinkedHashMap<String, CommonProfile>) context.getSessionStore().get(context, Pac4jConstants.USER_PROFILES);
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
        final CommonProfile profile = new CommonProfile();
        profile.setClientName(NAME);
        final MockIndirectClient client = new MockIndirectClient(NAME);
        client.setCallbackUrl(PAC4J_BASE_URL);
        client.setLogoutActionBuilder((ctx, p, targetUrl) -> new FoundAction(CALLBACK_URL + "?p=" + targetUrl));
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
        final CommonProfile profile = new CommonProfile();
        profile.setClientName(NAME);
        final MockIndirectClient client = new MockIndirectClient(NAME);
        client.setCallbackUrl(PAC4J_BASE_URL);
        client.setLogoutActionBuilder((ctx, p, targetUrl) -> new FoundAction(CALLBACK_URL + "?p=" + targetUrl));
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
    public void testLogoutWithBadUrlNoDefaultUrl() {
        context.addRequestParameter(Pac4jConstants.URL, PATH);
        logoutUrlPattern = VALUE;
        call();
        assertEquals(204, action.getCode());
        assertEquals("", context.getResponseContent());
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
