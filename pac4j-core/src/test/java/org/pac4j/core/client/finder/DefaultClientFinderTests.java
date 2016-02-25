package org.pac4j.core.client.finder;

import org.junit.Test;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.MockBaseClient;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests {@link DefaultClientFinder}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultClientFinderTests implements TestsConstants {

    private final DefaultClientFinder finder = new DefaultClientFinder();

    @Test
    public void testBlankClientName() {
        final List<Client> currentClients = finder.find(new Clients(), MockWebContext.create(), "  ");
        assertEquals(0, currentClients.size());
    }

    @Test
    public void testClientOnRequestAllowed() {
        internalTestClientOnRequestAllowedList(NAME, NAME);
    }

    @Test(expected = TechnicalException.class)
    public void testBadClientOnRequest() {
        final Client client = new MockBaseClient(NAME);
        final Clients clients = new Clients(client);
        final WebContext context = MockWebContext.create().addRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER, FAKE_VALUE);
        finder.find(clients, context, NAME);
    }

    @Test
    public void testClientOnRequestAllowedList() {
        internalTestClientOnRequestAllowedList(NAME, FAKE_VALUE + "," + NAME);
    }

    @Test
    public void testClientOnRequestAllowedListCaseTrim() {
        internalTestClientOnRequestAllowedList("NaMe  ", FAKE_VALUE.toUpperCase() + "  ,       nAmE");
    }

    private void internalTestClientOnRequestAllowedList(final String parameterName, final String names) {
        final Client client = new MockBaseClient(NAME);
        final Clients clients = new Clients(client);
        final WebContext context = MockWebContext.create().addRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER, parameterName);
        final List<Client> currentClients = finder.find(clients, context, names);
        assertEquals(1, currentClients.size());
        assertEquals(client, currentClients.get(0));
    }

    @Test(expected = TechnicalException.class)
    public void testClientOnRequestNotAllowed() {
        final Client client1 = new MockBaseClient(NAME);
        final Client client2 = new MockBaseClient(CLIENT_NAME);
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create().addRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        finder.find(clients, context, CLIENT_NAME);
    }

    @Test(expected = TechnicalException.class)
    public void testClientOnRequestNotAllowedList() {
        final Client client1 = new MockBaseClient(NAME);
        final Client client2 = new MockBaseClient(CLIENT_NAME);
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create().addRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        finder.find(clients, context, CLIENT_NAME + "," + FAKE_VALUE);
    }

    @Test
    public void testNoClientOnRequest() {
        final Client client1 = new MockBaseClient(NAME);
        final Client client2 = new MockBaseClient(CLIENT_NAME);
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        final List<Client> currentClients = finder.find(clients, context, CLIENT_NAME);
        assertEquals(1, currentClients.size());
        assertEquals(client2, currentClients.get(0));
    }

    @Test(expected = TechnicalException.class)
    public void testNoClientOnRequestBadDefaultClient() {
        final Client client1 = new MockBaseClient(NAME);
        final Client client2 = new MockBaseClient(CLIENT_NAME);
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        finder.find(clients, context, FAKE_VALUE);
    }

    @Test
    public void testNoClientOnRequestList() {
        internalTestNoClientOnRequestList(CLIENT_NAME + "," + NAME);
    }

    @Test
    public void testNoClientOnRequestListBlankSpaces() {
        internalTestNoClientOnRequestList(CLIENT_NAME + " ," + NAME);
    }

    @Test
    public void testNoClientOnRequestListDifferentCase() {
        internalTestNoClientOnRequestList(CLIENT_NAME.toUpperCase() + "," + NAME);
    }

    @Test
    public void testNoClientOnRequestListUppercase() {
        internalTestNoClientOnRequestList(CLIENT_NAME.toUpperCase() + "," + NAME);
    }

    private void internalTestNoClientOnRequestList(final String names) {
        final Client client1 = new MockBaseClient(NAME);
        final Client client2 = new MockBaseClient(CLIENT_NAME);
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        final List<Client> currentClients = finder.find(clients, context, names);
        assertEquals(2, currentClients.size());
        assertEquals(client2, currentClients.get(0));
        assertEquals(client1, currentClients.get(1));
    }
}
