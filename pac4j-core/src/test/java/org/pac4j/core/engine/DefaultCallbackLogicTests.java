package org.pac4j.core.engine;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.adapter.FrameworkAdapter;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.MockDirectClient;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.MockCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.SeeOtherAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

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

    private String defaultUrl;

    private Boolean renewSession;

    private ClientFinder clientFinder;

    private HttpAction action;

    @Before
    public void setUp() {
        logic = new DefaultCallbackLogic();
        config = new Config();
        context = MockWebContext.create();
        config.setWebContextFactory(p -> context);
        sessionStore = new MockSessionStore();
        config.setSessionStoreFactory(p -> sessionStore);
        config.setHttpActionAdapter((act, ctx) -> { action = act; return null; });
        defaultUrl = null;
        renewSession = null;
        clientFinder = new DefaultCallbackClientFinder();
        HttpActionHelper.setUseModernHttpCodes(true);
    }

    private void call() {
        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);

        logic.perform(config, defaultUrl, renewSession, null, mock(FrameworkParameters.class));
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
        TestsHelper.expectException(this::call, TechnicalException.class, "context cannot be null");
    }

    @Test
    public void testNullHttpActionAdapter() {
        config.setHttpActionAdapter(null);
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
        val directClient = new MockDirectClient(NAME, Optional.of(new MockCredentials()), new CommonProfile());
        config.setClients(new Clients(directClient));
        TestsHelper.expectException(this::call, TechnicalException.class,
            "unable to find one indirect client for the callback: check the callback URL for a client name parameter or" +
                " suffix path or ensure that your configuration defaults to one indirect client");
    }

    @Test
    public void testCallback() {
        val originalSessionId = sessionStore.getSessionId(context, false);
        context.addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        val profile = new CommonProfile();
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, Optional.of(new MockCredentials()), profile);
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        call();
        val newSessionId = sessionStore.getSessionId(context, false);
        Map<String, CommonProfile> profiles =
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
        val originalSessionId = sessionStore.getSessionId(context, false);
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, new FoundAction(PAC4J_URL));
        context.addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        val profile = new CommonProfile();
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, Optional.of(new MockCredentials()), profile);
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        call();
        val newSessionId = sessionStore.getSessionId(context, false);
        Map<String, CommonProfile> profiles =
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
        val originalSessionId = sessionStore.getSessionId(context, true);
        context.addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        val profile = new CommonProfile();
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, Optional.of(new MockCredentials()), profile);
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        renewSession = false;
        call();
        val newSessionId = sessionStore.getSessionId(context, false);
        Map<String, CommonProfile> profiles =
            (LinkedHashMap<String, CommonProfile>) sessionStore.get(context, Pac4jConstants.USER_PROFILES).get();
        assertTrue(profiles.containsValue(profile));
        assertEquals(1, profiles.size());
        assertEquals(newSessionId, originalSessionId);
        assertEquals(302, action.getCode());
        assertEquals(Pac4jConstants.DEFAULT_URL_VALUE, ((FoundAction) action).getLocation());
    }
}
