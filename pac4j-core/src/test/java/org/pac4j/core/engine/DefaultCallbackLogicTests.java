package org.pac4j.core.engine;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.MockDirectClient;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.MockCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.SeeOtherAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.LinkedHashMap;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests {@link DefaultCallbackLogic}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class DefaultCallbackLogicTests implements TestsConstants {

    private DefaultCallbackLogic logic;

    private MockWebContext context;

    private MockSessionStore sessionStore;

    private Config config;

    private HttpActionAdapter httpActionAdapter;

    private String defaultUrl;

    private Boolean renewSession;

    private ClientFinder clientFinder;

    private HttpAction action;

    @Before
    public void setUp() {
        logic = new DefaultCallbackLogic();
        context = MockWebContext.create();
        sessionStore = new MockSessionStore();
        config = new Config();
        httpActionAdapter = (act, ctx) -> { action = act; return null; };
        defaultUrl = null;
        renewSession = null;
        clientFinder = new DefaultCallbackClientFinder();
        HttpActionHelper.setUseModernHttpCodes(true);
    }

    private void call() {
        logic.perform(context, sessionStore, config, httpActionAdapter, defaultUrl, renewSession, null);
        logic.setClientFinder(clientFinder);
    }

    @Test
    public void testNullConfig() {
        config = null;
        TestsHelper.expectException(this::call, TechnicalException.class, "config cannot be null");
    }

    @Test
    public void testNullContext() {
        context = null;
        TestsHelper.expectException(this::call, TechnicalException.class, "webContext cannot be null");
    }

    @Test
    public void testNullHttpActionAdapter() {
        httpActionAdapter = null;
        TestsHelper.expectException(this::call, TechnicalException.class, "httpActionAdapter cannot be null");
    }

    @Test
    public void testBlankDefaultUrl() {
        defaultUrl = Pac4jConstants.EMPTY_STRING;
        TestsHelper.expectException(this::call, TechnicalException.class, "defaultUrl cannot be blank");
    }

    @Test
    public void testNullClients() {
        config.setClients(null);
        TestsHelper.expectException(this::call, TechnicalException.class, "clients cannot be null");
    }

    @Test
    public void testDirectClient() {
        context.addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        final var directClient = new MockDirectClient(NAME, Optional.of(new MockCredentials()), new CommonProfile());
        config.setClients(new Clients(directClient));
        TestsHelper.expectException(this::call, TechnicalException.class,
            "unable to find one indirect client for the callback: check the callback URL for a client name parameter or" +
                " suffix path or ensure that your configuration defaults to one indirect client");
    }

    @Test
    public void testCallback() {
        final var originalSessionId = sessionStore.getSessionId(context, false);
        context.addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        final var profile = new CommonProfile();
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, Optional.of(new MockCredentials()), profile);
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        call();
        final var newSessionId = sessionStore.getSessionId(context, false);
        final var profiles =
            (LinkedHashMap<String, CommonProfile>) sessionStore.get(context, Pac4jConstants.USER_PROFILES).get();
        assertTrue(profiles.containsValue(profile));
        assertEquals(1, profiles.size());
        assertNotEquals(newSessionId, originalSessionId);
        assertEquals(302, action.getCode());
        assertEquals(Pac4jConstants.DEFAULT_URL_VALUE, ((FoundAction) action).getLocation());
    }

    @Test
    public void testCallbackWithOriginallyRequestedUrl() {
        internalTestCallbackWithOriginallyRequestedUrl(302);
    }

    @Test
    public void testCallbackWithOriginallyRequestedUrlAndPostRequest() {
        context.setRequestMethod("POST");
        internalTestCallbackWithOriginallyRequestedUrl(303);
    }

    private void internalTestCallbackWithOriginallyRequestedUrl(final int code) {
        final var originalSessionId = sessionStore.getSessionId(context, false);
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, new FoundAction(PAC4J_URL));
        context.addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        final var profile = new CommonProfile();
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, Optional.of(new MockCredentials()), profile);
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        call();
        final var newSessionId = sessionStore.getSessionId(context, false);
        final var profiles =
            (LinkedHashMap<String, CommonProfile>) sessionStore.get(context, Pac4jConstants.USER_PROFILES).get();
        assertTrue(profiles.containsValue(profile));
        assertEquals(1, profiles.size());
        assertNotEquals(newSessionId, originalSessionId);
        assertEquals(code, action.getCode());
        if (action instanceof SeeOtherAction) {
            assertEquals(PAC4J_URL, ((SeeOtherAction) action).getLocation());
        } else {
            assertEquals(PAC4J_URL, ((FoundAction) action).getLocation());
        }
    }

    @Test
    public void testCallbackNoRenew() {
        final var originalSessionId = sessionStore.getSessionId(context, true);
        context.addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        final var profile = new CommonProfile();
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, Optional.of(new MockCredentials()), profile);
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        renewSession = false;
        call();
        final var newSessionId = sessionStore.getSessionId(context, false);
        final var profiles =
            (LinkedHashMap<String, CommonProfile>) sessionStore.get(context, Pac4jConstants.USER_PROFILES).get();
        assertTrue(profiles.containsValue(profile));
        assertEquals(1, profiles.size());
        assertEquals(newSessionId, originalSessionId);
        assertEquals(302, action.getCode());
        assertEquals(Pac4jConstants.DEFAULT_URL_VALUE, ((FoundAction) action).getLocation());
    }
}
