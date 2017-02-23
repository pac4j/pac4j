package org.pac4j.core.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.http.AjaxRequestResolver;
import org.pac4j.core.http.UrlResolver;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.*;

/**
 * This class tests the {@link Clients} class.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
@SuppressWarnings("rawtypes")
public final class ClientsTests implements TestsConstants {

    private MockIndirectClient newFacebookClient() {
        return new MockIndirectClient("FacebookClient", RedirectAction.redirect(LOGIN_URL), (Credentials) null, new CommonProfile());
    }

    private MockIndirectClient newYahooClient() {
        return new MockIndirectClient("YahooClient", RedirectAction.redirect(LOGIN_URL), (Credentials) null, new CommonProfile());
    }

    @Test
    public void testMissingClient() {
        final Clients clientsGroup = new Clients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(clientsGroup, "clients cannot be null");
    }

    @Test
    public void testNoCallbackUrl() {
        MockIndirectClient facebookClient = newFacebookClient();
        final Clients clientsGroup = new Clients(facebookClient);
        clientsGroup.init();
        assertNull(facebookClient.getCallbackUrl());
    }

    @Test
    public void testTwoClients() {
        final MockIndirectClient facebookClient = newFacebookClient();
        final MockIndirectClient yahooClient = newYahooClient();
        final List<Client> clients = new ArrayList<>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        final Clients clientsGroup = new Clients();
        clientsGroup.setClientNameParameter(TYPE);
        clientsGroup.setClients(clients);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        assertNull(facebookClient.getCallbackUrl());
        assertNull(yahooClient.getCallbackUrl());
        clientsGroup.init();
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + facebookClient.getName(), facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + TYPE + "=" + yahooClient.getName(), yahooClient.getCallbackUrl());
        assertEquals(yahooClient,
                clientsGroup.findClient(MockWebContext.create().addRequestParameter(TYPE, yahooClient.getName())));
        assertEquals(yahooClient, clientsGroup.findClient(yahooClient.getName()));
    }

    @Test
    public void testDoubleInit() {
        final MockIndirectClient facebookClient = newFacebookClient();
        final Clients clientsGroup = new Clients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        clientsGroup.setClients(facebookClient);
        clientsGroup.init();
        final Clients clientsGroup2 = new Clients();
        clientsGroup2.setCallbackUrl(CALLBACK_URL);
        clientsGroup2.setClients(facebookClient);
        clientsGroup2.init();
        assertEquals(CALLBACK_URL + "?" + Clients.DEFAULT_CLIENT_NAME_PARAMETER + "=" + facebookClient.getName(),
                facebookClient.getCallbackUrl());
    }

    @Test
    public void testAllClients() {
        final MockIndirectClient facebookClient = newFacebookClient();
        final MockIndirectClient yahooClient = newYahooClient();
        final List<Client> clients = new ArrayList<>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        final Clients clientsGroup = new Clients();
        clientsGroup.setClients(clients);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        final List<Client> clients2 = clientsGroup.findAllClients();
        assertEquals(2, clients2.size());
        assertTrue(clients2.containsAll(clients));
    }

    @Test
    public void testClientWithCallbackUrl() {
        final MockIndirectClient facebookClient = newFacebookClient();
        facebookClient.setCallbackUrl(LOGIN_URL);
        final MockIndirectClient yahooClient = newYahooClient();
        final Clients group = new Clients(CALLBACK_URL, facebookClient, yahooClient);
        group.setClientNameParameter(KEY);
        group.init();
        assertEquals(LOGIN_URL + "?" + group.getClientNameParameter() + "=" + facebookClient.getName(),
                facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL + "?" + group.getClientNameParameter() + "=" + yahooClient.getName(),
                yahooClient.getCallbackUrl());
    }

    @Test
    public void testClientWithCallbackUrlWithoutIncludingClientName() {
        final MockIndirectClient facebookClient = newFacebookClient();
        facebookClient.setCallbackUrl(LOGIN_URL);
        facebookClient.setIncludeClientNameInCallbackUrl(false);
        final MockIndirectClient yahooClient = newYahooClient();
        yahooClient.setIncludeClientNameInCallbackUrl(false);
        final Clients group = new Clients(CALLBACK_URL, facebookClient, yahooClient);
        group.setClientNameParameter(KEY);
        group.init();
        assertEquals(LOGIN_URL, facebookClient.getCallbackUrl());
        assertEquals(CALLBACK_URL, yahooClient.getCallbackUrl());
    }

    @Test
    public void testByClass1() {
        internalTestByClass(false);
    }

    @Test
    public void testByClass2() {
        internalTestByClass(true);
    }

    private void internalTestByClass(final boolean fakeFirst) {
        final MockIndirectClient facebookClient = newFacebookClient();
        final MockDirectClient fakeClient = new MockDirectClient(NAME, (Credentials) null, null);
        final Clients clients;
        if (fakeFirst) {
            clients = new Clients(CALLBACK_URL, fakeClient, facebookClient);
        } else {
            clients = new Clients(CALLBACK_URL, facebookClient, fakeClient);
        }
        assertEquals(facebookClient, clients.findClient(MockIndirectClient.class));
        assertEquals(fakeClient, clients.findClient(MockDirectClient.class));
    }

    @Test
    public void rejectSameName() {
        final MockIndirectClient client1 = new MockIndirectClient(NAME, RedirectAction.redirect(LOGIN_URL), (Credentials) null, new CommonProfile());
        final MockIndirectClient client2 = new MockIndirectClient(NAME, RedirectAction.redirect(LOGIN_URL), (Credentials) null, new CommonProfile());
        final Clients clients = new Clients(CALLBACK_URL, client1, client2);
        TestsHelper.initShouldFail(clients, "Duplicate name in clients: name");
    }

    @Test
    public void rejectSameNameDifferentCase() {
        final MockIndirectClient client1 = new MockIndirectClient(NAME, RedirectAction.redirect(LOGIN_URL), (Credentials) null, new CommonProfile());
        final MockIndirectClient client2 = new MockIndirectClient(NAME.toUpperCase(), RedirectAction.redirect(LOGIN_URL), (Credentials) null, new CommonProfile());
        final Clients clients = new Clients(CALLBACK_URL, client1, client2);
        TestsHelper.initShouldFail(clients, "Duplicate name in clients: NAME");
    }

    @Test
    public void testFindByName() {
        final MockIndirectClient facebookClient = newFacebookClient();
        final MockIndirectClient yahooClient = newYahooClient();
        final Clients clients = new Clients(facebookClient, yahooClient);
        assertNotNull(clients.findClient("FacebookClient"));
    }

    @Test
    public void testFindByNameCase() {
        final MockIndirectClient facebookClient = newFacebookClient();
        final MockIndirectClient yahooClient = newYahooClient();
        final Clients clients = new Clients(facebookClient, yahooClient);
        assertNotNull(clients.findClient("FACEBOOKclient"));
    }

    @Test
    public void testFindByNameBlankSpaces() {
        final MockIndirectClient facebookClient = newFacebookClient();
        final MockIndirectClient yahooClient = newYahooClient();
        final Clients clients = new Clients(facebookClient, yahooClient);
        assertNotNull(clients.findClient(" FacebookClient          "));
    }

    @Test
    public void testDefineAjaxCallbackResolverAuthGenerator() {
        final AjaxRequestResolver ajaxRequestResolver = ctx -> false;
        final UrlResolver urlResolver = (url, ctx) -> url;
        final AuthorizationGenerator authorizationGenerator = profile -> {};
        final MockIndirectClient facebookClient = newFacebookClient();
        final Clients clients = new Clients(CALLBACK_URL, facebookClient);
        clients.setAjaxRequestResolver(ajaxRequestResolver);
        clients.setUrlResolver(urlResolver);
        clients.addAuthorizationGenerator(authorizationGenerator);
        clients.init();
        assertEquals(ajaxRequestResolver, facebookClient.getAjaxRequestResolver());
        assertEquals(urlResolver, facebookClient.getUrlResolver());
        assertEquals(authorizationGenerator, facebookClient.getAuthorizationGenerators().get(0));
    }
}
