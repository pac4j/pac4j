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
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.credentials.MockCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link DefaultCallbackLogic}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class DefaultCallbackLogicTests implements TestsConstants {

    private DefaultCallbackLogic<Object, JEEContext> logic;

    protected MockHttpServletRequest request;

    protected MockHttpServletResponse response;

    private JEEContext context;

    private Config config;

    private HttpActionAdapter<Object, JEEContext> httpActionAdapter;

    private String defaultUrl;

    private Boolean renewSession;

    private ClientFinder clientFinder;

    @Before
    public void setUp() {
        logic = new DefaultCallbackLogic<>();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        context = new JEEContext(request, response);
        config = new Config();
        httpActionAdapter = (code, ctx) -> null;
        defaultUrl = null;
        renewSession = null;
        clientFinder = new DefaultCallbackClientFinder();
    }

    private void call() {
        logic.perform(context, config, httpActionAdapter, defaultUrl, null, null, renewSession, null);
        logic.setClientFinder(clientFinder);
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
    public void testBlankDefaultUrl() {
        defaultUrl = "";
        TestsHelper.expectException(() -> call(), TechnicalException.class, "defaultUrl cannot be blank");
    }

    @Test
    public void testNullClients() {
        config.setClients(null);
        TestsHelper.expectException(() -> call(), TechnicalException.class, "clients cannot be null");
    }

    @Test
    public void testNullClientFinder() {
        clientFinder = null;
        TestsHelper.expectException(() -> call(), TechnicalException.class, "clients cannot be null");
    }

    @Test
    public void testDirectClient() {
        request.addParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        final MockDirectClient directClient = new MockDirectClient(NAME, new MockCredentials(), new CommonProfile());
        config.setClients(new Clients(directClient));
        TestsHelper.expectException(() -> call(), TechnicalException.class,
            "unable to find one indirect client for the callback: check the callback URL for a client name parameter or" +
                " suffix path or ensure that your configuration defaults to one indirect client");
    }

    @Test
    public void testCallback() {
        final String originalSessionId = request.getSession().getId();
        request.setParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        final CommonProfile profile = new CommonProfile();
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, new MockCredentials(), profile);
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        config.getClients().init();
        call();
        final HttpSession session = request.getSession();
        final String newSessionId = session.getId();
        final LinkedHashMap<String, CommonProfile> profiles =
            (LinkedHashMap<String, CommonProfile>) session.getAttribute(Pac4jConstants.USER_PROFILES);
        assertTrue(profiles.containsValue(profile));
        assertEquals(1, profiles.size());
        assertNotEquals(newSessionId, originalSessionId);
        assertEquals(302, response.getStatus());
        assertEquals(Pac4jConstants.DEFAULT_URL_VALUE, response.getRedirectedUrl());
    }

    @Test
    public void testCallbackWithOriginallyRequestedUrl() {
        HttpSession session = request.getSession();
        final String originalSessionId = session.getId();
        session.setAttribute(Pac4jConstants.REQUESTED_URL, PAC4J_URL);
        request.setParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        final CommonProfile profile = new CommonProfile();
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, new MockCredentials(), profile);
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        config.getClients().init();
        call();
        session = request.getSession();
        final String newSessionId = session.getId();
        final LinkedHashMap<String, CommonProfile> profiles =
            (LinkedHashMap<String, CommonProfile>) session.getAttribute(Pac4jConstants.USER_PROFILES);
        assertTrue(profiles.containsValue(profile));
        assertEquals(1, profiles.size());
        assertNotEquals(newSessionId, originalSessionId);
        assertEquals(302, response.getStatus());
        assertEquals(PAC4J_URL, response.getRedirectedUrl());
    }

    @Test
    public void testCallbackNoRenew() {
        final String originalSessionId = request.getSession().getId();
        request.setParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        final CommonProfile profile = new CommonProfile();
        final IndirectClient indirectClient = new MockIndirectClient(NAME, null, new MockCredentials(), profile);
        config.setClients(new Clients(CALLBACK_URL, indirectClient));
        renewSession = false;
        config.getClients().init();
        call();
        final HttpSession session = request.getSession();
        final String newSessionId = session.getId();
        final LinkedHashMap<String, CommonProfile> profiles =
            (LinkedHashMap<String, CommonProfile>) session.getAttribute(Pac4jConstants.USER_PROFILES);
        assertTrue(profiles.containsValue(profile));
        assertEquals(1, profiles.size());
        assertEquals(newSessionId, originalSessionId);
        assertEquals(302, response.getStatus());
        assertEquals(Pac4jConstants.DEFAULT_URL_VALUE, response.getRedirectedUrl());
    }
}
