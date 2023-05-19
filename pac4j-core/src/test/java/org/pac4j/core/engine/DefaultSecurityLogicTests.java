package org.pac4j.core.engine;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.adapter.FrameworkAdapter;
import org.pac4j.core.client.*;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.MockCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.StatusAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link DefaultSecurityLogic}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class DefaultSecurityLogicTests implements TestsConstants {

    private DefaultSecurityLogic logic;

    private MockWebContext context;

    private SessionStore sessionStore;

    private Config config;

    private SecurityGrantedAccessAdapter securityGrantedAccessAdapter;

    private String clients;

    private String authorizers;

    private String matchers;

    private int nbCall;

    private HttpAction action;

    @Before
    public void setUp() {
        logic = new DefaultSecurityLogic();
        config = new Config();
        context = MockWebContext.create();
        config.setWebContextFactory(p -> context);
        sessionStore = new MockSessionStore();
        config.setSessionStoreFactory(p -> sessionStore);
        securityGrantedAccessAdapter = (context, sessionStore, profiles) -> { nbCall++; return null; };
        config.setHttpActionAdapter((act, ctx) -> { action = act; return null; });
        clients = null;
        authorizers = null;
        matchers = null;
        nbCall = 0;
    }

    private void call() {
        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);

        logic.perform(config, securityGrantedAccessAdapter, clients, authorizers, matchers, mock(FrameworkParameters.class));
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
    public void testNullClients() {
        config.setClients(null);
        TestsHelper.expectException(this::call, TechnicalException.class, "configClients cannot be null");
    }

    @Test
    public void testNullClientFinder() {
        logic.setClientFinder(null);
        TestsHelper.expectException(this::call, TechnicalException.class, "clientFinder cannot be null");
    }

    @Test
    public void testNullAuthorizationChecker() {
        logic.setAuthorizationChecker(null);
        TestsHelper.expectException(this::call, TechnicalException.class, "authorizationChecker cannot be null");
    }

    @Test
    public void testNullMatchingChecker() {
        logic.setMatchingChecker(null);
        TestsHelper.expectException(this::call, TechnicalException.class, "matchingChecker cannot be null");
    }

    @Test
    public void testNotAuthenticated() {
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, Optional.of(new MockCredentials()), new CommonProfile());
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        clients = Pac4jConstants.EMPTY_STRING;
        call();
        assertEquals(401, action.getCode());
    }

    @Test
    public void testNotAuthenticatedButMatcher() {
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, Optional.of(new MockCredentials()), new CommonProfile());
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        config.addMatcher(NAME, ctx -> false);
        matchers = NAME;
        call();
        assertNull(action);
        assertEquals(1, nbCall);
    }

    @Test
    public void testForceAuthentication() {
        this.logic.setLoadProfilesFromSession(false);
        var profile = new CommonProfile();
        profile.setId(ID);
        Map<String, CommonProfile> profiles = new LinkedHashMap<>();
        profiles.put(NAME, profile);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        var indirectClient = new MockIndirectClient(NAME,
            new FoundAction(PAC4J_URL), Optional.of(new MockCredentials()), new CommonProfile());
        authorizers = NAME;
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        config.addAuthorizer(NAME, (context, store, prof) -> ID.equals(prof.get(0).getId()));
        call();
        assertNotNull(action);
        assertEquals(302, action.getCode());
    }

    @Test
    public void testAlreadyAuthenticatedAndAuthorized() {
        val profile = new CommonProfile();
        profile.setId(ID);
        Map<String, CommonProfile> profiles = new LinkedHashMap<>();
        profiles.put(NAME, profile);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, Optional.of(new MockCredentials()), new CommonProfile());
        authorizers = NAME;
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        config.addAuthorizer(NAME, (context, store, prof) -> ID.equals(prof.get(0).getId()));
        call();
        assertNull(action);
        assertEquals(1, nbCall);
    }

    @Test
    public void testAlreadyAuthenticatedNotAuthorized() {
        val profile = new CommonProfile();
        Map<String, CommonProfile> profiles = new LinkedHashMap<>();
        profiles.put(NAME, profile);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, Optional.of(new MockCredentials()), new CommonProfile());
        authorizers = NAME;
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        config.addAuthorizer(NAME, (context, store, prof) -> ID.equals(prof.get(0).getId()));
        call();
        assertEquals(403, action.getCode());
    }

    @Test
    public void testAuthorizerThrowsRequiresHttpAction() {
        val profile = new CommonProfile();
        Map<String, CommonProfile> profiles = new LinkedHashMap<>();
        profiles.put(NAME, profile);
        sessionStore.set(context, Pac4jConstants.USER_PROFILES, profiles);
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, Optional.of(new MockCredentials()), new CommonProfile());
        authorizers = NAME;
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        config.addAuthorizer(NAME, (context, store, prof) -> { throw new StatusAction(400); } );
        call();
        assertEquals(400, action.getCode());
        assertEquals(0, nbCall);
    }

    @Test
    public void testDoubleDirectClient() {
        val profile = new CommonProfile();
        profile.setId(NAME);
        val profile2 = new CommonProfile();
        profile2.setId(VALUE);
        final DirectClient directClient = new MockDirectClient(NAME, Optional.of(new MockCredentials()), profile);
        final DirectClient directClient2 = new MockDirectClient(VALUE, Optional.of(new MockCredentials()), profile2);
        config.setClients(new Clients(CALLBACK_URL, directClient, directClient2));
        clients = NAME + "," + VALUE;
        call();
        assertEquals(-1, context.getResponseStatus());
        assertEquals(1, nbCall);
        Map<String, CommonProfile> profiles =
            (LinkedHashMap<String, CommonProfile>) context.getRequestAttribute(Pac4jConstants.USER_PROFILES).get();
        assertEquals(1, profiles.size());
        assertTrue(profiles.containsValue(profile));
    }

    @Test
    public void testDirectClientThrowsRequiresHttpAction() {
        val profile = new CommonProfile();
        profile.setId(NAME);
        final DirectClient directClient = new MockDirectClient(NAME, () -> { throw new StatusAction(400); },
            profile);
        config.setClients(new Clients(CALLBACK_URL, directClient));
        clients = NAME;
        call();
        assertEquals(400, action.getCode());
        assertEquals(0, nbCall);
    }

    @Test
    public void testDoubleDirectClientSupportingMultiProfile() {
        val profile = new CommonProfile();
        profile.setId(NAME);
        val profile2 = new CommonProfile();
        profile2.setId(VALUE);
        final DirectClient directClient = new MockDirectClient(NAME, Optional.of(new MockCredentials()), profile);
        directClient.setMultiProfile(true);
        final DirectClient directClient2 = new MockDirectClient(VALUE, Optional.of(new MockCredentials()), profile2);
        directClient2.setMultiProfile(true);
        config.setClients(new Clients(CALLBACK_URL, directClient, directClient2));
        clients = NAME + "," + VALUE;
        call();
        assertEquals(-1, context.getResponseStatus());
        assertEquals(1, nbCall);
        Map<String, CommonProfile> profiles =
            (LinkedHashMap<String, CommonProfile>) context.getRequestAttribute(Pac4jConstants.USER_PROFILES).get();
        assertEquals(2, profiles.size());
        assertTrue(profiles.containsValue(profile));
        assertTrue(profiles.containsValue(profile2));
    }

    @Test
    public void testDoubleDirectClientChooseDirectClient() {
        val profile = new CommonProfile();
        profile.setId(NAME);
        val profile2 = new CommonProfile();
        profile2.setId(VALUE);
        final DirectClient directClient = new MockDirectClient(NAME, Optional.of(new MockCredentials()), profile);
        final DirectClient directClient2 = new MockDirectClient(VALUE, Optional.of(new MockCredentials()), profile2);
        config.setClients(new Clients(CALLBACK_URL, directClient, directClient2));
        clients = NAME + "," + VALUE;
        context.addRequestParameter(Pac4jConstants.DEFAULT_FORCE_CLIENT_PARAMETER, VALUE);
        call();
        assertEquals(-1, context.getResponseStatus());
        assertEquals(1, nbCall);
        Map<String, CommonProfile> profiles =
            (LinkedHashMap<String, CommonProfile>) context.getRequestAttribute(Pac4jConstants.USER_PROFILES).get();
        assertEquals(1, profiles.size());
        assertTrue(profiles.containsValue(profile2));
    }

    @Test
    public void testDoubleDirectClientChooseBadDirectClient() {
        val profile = new CommonProfile();
        profile.setId(NAME);
        val profile2 = new CommonProfile();
        profile2.setId(VALUE);
        final DirectClient directClient = new MockDirectClient(NAME, Optional.of(new MockCredentials()), profile);
        final DirectClient directClient2 = new MockDirectClient(VALUE, Optional.of(new MockCredentials()), profile2);
        config.setClients(new Clients(CALLBACK_URL, directClient, directClient2));
        clients = NAME;
        context.addRequestParameter(Pac4jConstants.DEFAULT_FORCE_CLIENT_PARAMETER, VALUE);
        call();
        assertEquals(401, action.getCode());
    }

    @Test
    public void testRedirectByIndirectClient() {
        final IndirectClient indirectClient =
            new MockIndirectClient(NAME, new FoundAction(PAC4J_URL), Optional.of(new MockCredentials()), new CommonProfile());
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        clients = NAME;
        call();
        assertEquals(302, action.getCode());
        assertEquals(PAC4J_URL, ((FoundAction) action).getLocation());
    }

    @Test
    public void testDoubleIndirectClientOneChosen() {
        final IndirectClient indirectClient =
            new MockIndirectClient(NAME, new FoundAction(PAC4J_URL), Optional.of(new MockCredentials()), new CommonProfile());
        final IndirectClient indirectClient2 =
            new MockIndirectClient(VALUE, new FoundAction(PAC4J_BASE_URL), Optional.of(new MockCredentials()), new CommonProfile());
        config.setClients(new Clients(CALLBACK_URL, indirectClient, indirectClient2));
        clients = NAME + "," + VALUE;
        context.addRequestParameter(Pac4jConstants.DEFAULT_FORCE_CLIENT_PARAMETER, VALUE);
        call();
        assertEquals(302, action.getCode());
        assertEquals(PAC4J_BASE_URL, ((FoundAction) action).getLocation());
    }

    @Test
    public void testDoubleIndirectClientBadOneChosen() {
        final IndirectClient indirectClient =
            new MockIndirectClient(NAME, new FoundAction(PAC4J_URL), Optional.of(new MockCredentials()), new CommonProfile());
        final IndirectClient indirectClient2 =
            new MockIndirectClient(VALUE, new FoundAction(PAC4J_BASE_URL), Optional.of(new MockCredentials()), new CommonProfile());
        config.setClients(new Clients(CALLBACK_URL, indirectClient, indirectClient2));
        clients = NAME;
        context.addRequestParameter(Pac4jConstants.DEFAULT_FORCE_CLIENT_PARAMETER, VALUE);
        call();
        assertEquals(401, action.getCode());
    }
}
