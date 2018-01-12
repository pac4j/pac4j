package org.pac4j.core.engine;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.client.*;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.MockCredentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.LinkedHashMap;

import static org.junit.Assert.*;

/**
 * Tests {@link DefaultSecurityLogic}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class DefaultSecurityLogicTests implements TestsConstants {

    private DefaultSecurityLogic<Object, WebContext> logic;

    private MockWebContext context;

    private Config config;

    private SecurityGrantedAccessAdapter<Object, WebContext> securityGrantedAccessAdapter;

    private HttpActionAdapter<Object, WebContext> httpActionAdapter;

    private String clients;

    private String authorizers;

    private String matchers;

    private Boolean multiProfile;

    private int nbCall;

    @Before
    public void setUp() {
        logic = new DefaultSecurityLogic();
        context = MockWebContext.create();
        config = new Config();
        securityGrantedAccessAdapter = (context, profiles, parameters) -> { nbCall++; return null; };
        httpActionAdapter = (code, ctx) -> null;
        clients = null;
        authorizers = null;
        matchers = null;
        multiProfile = null;
        nbCall = 0;
    }

    private void call() {
        logic.perform(context, config, securityGrantedAccessAdapter, httpActionAdapter, clients, authorizers, matchers, multiProfile);
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
    public void testNullClients() {
        config.setClients(null);
        TestsHelper.expectException(() -> call(), TechnicalException.class, "configClients cannot be null");
    }

    @Test
    public void testNullClientFinder() {
        logic.setClientFinder(null);
        TestsHelper.expectException(() -> call(), TechnicalException.class, "clientFinder cannot be null");
    }

    @Test
    public void testNullAuthorizationChecker() {
        logic.setAuthorizationChecker(null);
        TestsHelper.expectException(() -> call(), TechnicalException.class, "authorizationChecker cannot be null");
    }

    @Test
    public void testNullMatchingChecker() {
        logic.setMatchingChecker(null);
        TestsHelper.expectException(() -> call(), TechnicalException.class, "matchingChecker cannot be null");
    }

    @Test
    public void testNotAuthenticated() {
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, new MockCredentials(), new CommonProfile());
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        clients = "";
        call();
        assertEquals(401, context.getResponseStatus());
    }

    @Test
    public void testNotAuthenticatedButMatcher() {
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, new MockCredentials(), new CommonProfile());
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        config.addMatcher(NAME, context -> false);
        matchers = NAME;
        call();
        assertEquals(-1, context.getResponseStatus());
        assertEquals(1, nbCall);
    }

    @Test
    public void testAlreadyAuthenticatedAndAuthorized() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(ID);
        final LinkedHashMap<String, CommonProfile> profiles = new LinkedHashMap<>();
        profiles.put(NAME, profile);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, new MockCredentials(), new CommonProfile());
        authorizers = NAME;
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        config.addAuthorizer(NAME, (context, prof) -> ID.equals(((CommonProfile) prof.get(0)).getId()));
        call();
        assertEquals(-1, context.getResponseStatus());
        assertEquals(1, nbCall);
    }

    @Test
    public void testAlreadyAuthenticatedNotAuthorized() {
        final CommonProfile profile = new CommonProfile();
        final LinkedHashMap<String, CommonProfile> profiles = new LinkedHashMap<>();
        profiles.put(NAME, profile);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, new MockCredentials(), new CommonProfile());
        authorizers = NAME;
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        config.addAuthorizer(NAME, (context, prof) -> ID.equals(((CommonProfile) prof.get(0)).getId()));
        call();
        assertEquals(403, context.getResponseStatus());
    }

    @Test
    public void testAuthorizerThrowsRequiresHttpAction() {
        final CommonProfile profile = new CommonProfile();
        final LinkedHashMap<String, CommonProfile> profiles = new LinkedHashMap<>();
        profiles.put(NAME, profile);
        context.getSessionStore().set(context, Pac4jConstants.USER_PROFILES, profiles);
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, new MockCredentials(), new CommonProfile());
        authorizers = NAME;
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        config.addAuthorizer(NAME, (context, prof) -> { throw HttpAction.status(400, context); } );
        call();
        assertEquals(400, context.getResponseStatus());
        assertEquals(0, nbCall);
    }

    @Test
    public void testDoubleDirectClient() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(NAME);
        final CommonProfile profile2 = new CommonProfile();
        profile2.setId(VALUE);
        final DirectClient directClient = new MockDirectClient(NAME, new MockCredentials(), profile);
        final DirectClient directClient2 = new MockDirectClient(VALUE, new MockCredentials(), profile2);
        config.setClients(new Clients(CALLBACK_URL, directClient, directClient2));
        clients = NAME + "," + VALUE;
        call();
        assertEquals(-1, context.getResponseStatus());
        assertEquals(1, nbCall);
        final LinkedHashMap<String, CommonProfile> profiles =
            (LinkedHashMap<String, CommonProfile>) context.getRequestAttribute(Pac4jConstants.USER_PROFILES);
        assertEquals(1, profiles.size());
        assertTrue(profiles.containsValue(profile));
    }

    @Test
    public void testDirectClientThrowsRequiresHttpAction() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(NAME);
        final DirectClient directClient = new MockDirectClient(NAME, () -> { throw HttpAction.status(400, context); },
            profile);
        config.setClients(new Clients(CALLBACK_URL, directClient));
        clients = NAME;
        call();
        assertEquals(400, context.getResponseStatus());
        assertEquals(0, nbCall);
    }

    @Test
    public void testDoubleDirectClientSupportingMultiProfile() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(NAME);
        final CommonProfile profile2 = new CommonProfile();
        profile2.setId(VALUE);
        final DirectClient directClient = new MockDirectClient(NAME, new MockCredentials(), profile);
        final DirectClient directClient2 = new MockDirectClient(VALUE, new MockCredentials(), profile2);
        config.setClients(new Clients(CALLBACK_URL, directClient, directClient2));
        clients = NAME + "," + VALUE;
        multiProfile = true;
        call();
        assertEquals(-1, context.getResponseStatus());
        assertEquals(1, nbCall);
        final LinkedHashMap<String, CommonProfile> profiles =
            (LinkedHashMap<String, CommonProfile>) context.getRequestAttribute(Pac4jConstants.USER_PROFILES);
        assertEquals(2, profiles.size());
        assertTrue(profiles.containsValue(profile));
        assertTrue(profiles.containsValue(profile2));
    }

    @Test
    public void testDoubleDirectClientChooseDirectClient() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(NAME);
        final CommonProfile profile2 = new CommonProfile();
        profile2.setId(VALUE);
        final DirectClient directClient = new MockDirectClient(NAME, new MockCredentials(), profile);
        final DirectClient directClient2 = new MockDirectClient(VALUE, new MockCredentials(), profile2);
        config.setClients(new Clients(CALLBACK_URL, directClient, directClient2));
        clients = NAME + "," + VALUE;
        context.addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, VALUE);
        multiProfile = true;
        call();
        assertEquals(-1, context.getResponseStatus());
        assertEquals(1, nbCall);
        final LinkedHashMap<String, CommonProfile> profiles =
            (LinkedHashMap<String, CommonProfile>) context.getRequestAttribute(Pac4jConstants.USER_PROFILES);
        assertEquals(1, profiles.size());
        assertTrue(profiles.containsValue(profile2));
    }

    @Test
    public void testDoubleDirectClientChooseBadDirectClient() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(NAME);
        final CommonProfile profile2 = new CommonProfile();
        profile2.setId(VALUE);
        final DirectClient directClient = new MockDirectClient(NAME, new MockCredentials(), profile);
        final DirectClient directClient2 = new MockDirectClient(VALUE, new MockCredentials(), profile2);
        config.setClients(new Clients(CALLBACK_URL, directClient, directClient2));
        clients = NAME;
        context.addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, VALUE);
        multiProfile = true;
        TestsHelper.expectException(() -> call(), TechnicalException.class, "Client not allowed: " + VALUE);
    }

    @Test
    public void testRedirectByIndirectClient() {
        final IndirectClient indirectClient =
            new MockIndirectClient(NAME, RedirectAction.redirect(PAC4J_URL), new MockCredentials(), new CommonProfile());
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        clients = NAME;
        call();
        assertEquals(302, context.getResponseStatus());
        assertEquals(PAC4J_URL, context.getResponseLocation());
    }

    @Test
    public void testDoubleIndirectClientOneChosen() {
        final IndirectClient indirectClient =
            new MockIndirectClient(NAME, RedirectAction.redirect(PAC4J_URL), new MockCredentials(), new CommonProfile());
        final IndirectClient indirectClient2 =
            new MockIndirectClient(VALUE, RedirectAction.redirect(PAC4J_BASE_URL), new MockCredentials(), new CommonProfile());
        config.setClients(new Clients(CALLBACK_URL, indirectClient, indirectClient2));
        clients = NAME + "," + VALUE;
        context.addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, VALUE);
        call();
        assertEquals(302, context.getResponseStatus());
        assertEquals(PAC4J_BASE_URL, context.getResponseLocation());
    }

    @Test
    public void testDoubleIndirectClientBadOneChosen() {
        final IndirectClient indirectClient =
            new MockIndirectClient(NAME, RedirectAction.redirect(PAC4J_URL), new MockCredentials(), new CommonProfile());
        final IndirectClient indirectClient2 =
            new MockIndirectClient(VALUE, RedirectAction.redirect(PAC4J_BASE_URL), new MockCredentials(), new CommonProfile());
        config.setClients(new Clients(CALLBACK_URL, indirectClient, indirectClient2));
        clients = NAME;
        context.addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, VALUE);
        TestsHelper.expectException(() -> call(), TechnicalException.class, "Client not allowed: " + VALUE);
    }
}
