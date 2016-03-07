package org.pac4j.core.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
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

    private MockBaseClient newFacebookClient() {
        return new MockBaseClient("FacebookClient");
    }

    private MockBaseClient newYahooClient() {
        return new MockBaseClient("YahooClient");
    }

    @Test
    public void testMissingClient() {
        final Clients clientsGroup = new Clients();
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(clientsGroup, "clients cannot be null");
    }

    @Test
    public void testNoCallbackUrl() {
        MockBaseClient facebookClient = newFacebookClient();
        final Clients clientsGroup = new Clients(facebookClient);
        clientsGroup.init();
        assertNull(facebookClient.getCallbackUrl());
    }

    @Test
    public void testTwoClients() {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
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
        final MockBaseClient facebookClient = newFacebookClient();
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
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
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
        final MockBaseClient facebookClient = newFacebookClient();
        facebookClient.setCallbackUrl(LOGIN_URL);
        final MockBaseClient yahooClient = newYahooClient();
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
        final MockBaseClient facebookClient = newFacebookClient();
        facebookClient.setCallbackUrl(LOGIN_URL);
        facebookClient.setIncludeClientNameInCallbackUrl(false);
        final MockBaseClient yahooClient = newYahooClient();
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
        final MockBaseClient facebookClient = newFacebookClient();
        final FakeClient fakeClient = new FakeClient();
        final Clients clients;
        if (fakeFirst) {
            clients = new Clients(CALLBACK_URL, fakeClient, facebookClient);
        } else {
            clients = new Clients(CALLBACK_URL, facebookClient, fakeClient);
        }
        assertEquals(facebookClient, clients.findClient(MockBaseClient.class));
        assertEquals(fakeClient, clients.findClient(FakeClient.class));
    }

    @Test
    public void rejectSameName() {
        final MockBaseClient client1 = new MockBaseClient(NAME);
        final MockBaseClient client2 = new MockBaseClient(NAME);
        final Clients clients = new Clients(CALLBACK_URL, client1, client2);
        TestsHelper.initShouldFail(clients, "Duplicate name in clients: name");
    }

    @Test
    public void rejectSameNameDifferentCase() {
        final MockBaseClient client1 = new MockBaseClient(NAME);
        final MockBaseClient client2 = new MockBaseClient(NAME.toUpperCase());
        final Clients clients = new Clients(CALLBACK_URL, client1, client2);
        TestsHelper.initShouldFail(clients, "Duplicate name in clients: NAME");
    }

    @Test
    public void testFindByName() {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
        final Clients clients = new Clients(facebookClient, yahooClient);
        assertNotNull(clients.findClient("FacebookClient"));
    }

    @Test
    public void testFindByNameCase() {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
        final Clients clients = new Clients(facebookClient, yahooClient);
        assertNotNull(clients.findClient("FACEBOOKclient"));
    }

    @Test
    public void testFindByNameBlankSpaces() {
        final MockBaseClient facebookClient = newFacebookClient();
        final MockBaseClient yahooClient = newYahooClient();
        final Clients clients = new Clients(facebookClient, yahooClient);
        assertNotNull(clients.findClient(" FacebookClient          "));
    }

    private static class FakeClient extends DirectClient<Credentials, CommonProfile> {

        @Override
        public Credentials getCredentials(final WebContext context) throws RequiresHttpAction {
            throw new UnsupportedOperationException("Not implemneted yet");
        }

        @Override
        protected CommonProfile retrieveUserProfile(final Credentials credentials, final WebContext context) {
            throw new UnsupportedOperationException("Not implemneted yet");
        }

        @Override
        protected void internalInit(final WebContext context) {
        }
    }
}
