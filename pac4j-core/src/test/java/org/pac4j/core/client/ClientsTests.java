package org.pac4j.core.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.callback.QueryParameterCallbackUrlResolver;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.profile.CommonProfile;
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
        return new MockIndirectClient("FacebookClient", new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
    }

    private MockIndirectClient newYahooClient() {
        return new MockIndirectClient("YahooClient", new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
    }

    @Test
    public void testMissingClient() {
        final Clients clients = new Clients();
        clients.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(clients, "clients cannot be null");
    }

    @Test
    public void testNoValuesSet() {
        MockIndirectClient facebookClient = newFacebookClient();
        final Clients clients = new Clients(facebookClient);
        clients.init();
        assertNull(facebookClient.getCallbackUrl());
        assertNull(facebookClient.getUrlResolver());
        assertNull(facebookClient.getCallbackUrlResolver());
        assertNull(facebookClient.getAjaxRequestResolver());
        assertEquals(0, facebookClient.getAuthorizationGenerators().size());
    }

    @Test
    public void testValuesSet() {
        MockIndirectClient facebookClient = newFacebookClient();
        final Clients clients = new Clients(facebookClient);
        final AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();
        final UrlResolver urlResolver = new DefaultUrlResolver();
        final CallbackUrlResolver callbackUrlResolver = new QueryParameterCallbackUrlResolver();
        final AuthorizationGenerator authorizationGenerator = (context, profile) -> profile;
        clients.setCallbackUrl(CALLBACK_URL);
        clients.setAjaxRequestResolver(ajaxRequestResolver);
        clients.setUrlResolver(urlResolver);
        clients.setCallbackUrlResolver(callbackUrlResolver);
        clients.addAuthorizationGenerator(authorizationGenerator);
        clients.init();
        assertEquals(CALLBACK_URL, facebookClient.getCallbackUrl());
        assertEquals(urlResolver, facebookClient.getUrlResolver());
        assertEquals(callbackUrlResolver, facebookClient.getCallbackUrlResolver());
        assertEquals(ajaxRequestResolver, facebookClient.getAjaxRequestResolver());
        assertEquals(authorizationGenerator, facebookClient.getAuthorizationGenerators().get(0));
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
        final MockIndirectClient client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final MockIndirectClient client2 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final Clients clients = new Clients(CALLBACK_URL, client1, client2);
        TestsHelper.initShouldFail(clients, "Duplicate name in clients: name");
    }

    @Test
    public void rejectSameNameDifferentCase() {
        final MockIndirectClient client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final MockIndirectClient client2 =
            new MockIndirectClient(NAME.toUpperCase(), new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
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
}
