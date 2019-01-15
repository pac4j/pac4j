package org.pac4j.core.client.finder;

import org.junit.Test;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests {@link DefaultSecurityClientFinder}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultSecurityClientFinderTests implements TestsConstants {

    private final DefaultSecurityClientFinder finder = new DefaultSecurityClientFinder();

    @Test
    public void testBlankClientName() {
        final List<Client> currentClients = finder.find(new Clients(), MockWebContext.create(), "  ");
        assertEquals(0, currentClients.size());
    }

    @Test
    public void testClientOnRequestAllowed() {
        internalTestClientOnRequestAllowedList(NAME, NAME);
    }

    @Test
    public void testBadClientOnRequest() {
        final MockIndirectClient client =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final Clients clients = new Clients(client);
        final WebContext context = MockWebContext.create().addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, FAKE_VALUE);
        assertTrue(finder.find(clients, context, NAME).isEmpty());
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
        final MockIndirectClient client =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final Clients clients = new Clients(client);
        final WebContext context = MockWebContext.create().addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, parameterName);
        final List<Client> currentClients = finder.find(clients, context, names);
        assertEquals(1, currentClients.size());
        assertEquals(client, currentClients.get(0));
    }

    @Test
    public void testClientOnRequestNotAllowed() {
        final MockIndirectClient client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final MockIndirectClient client2 =
            new MockIndirectClient(CLIENT_NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create().addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        assertTrue(finder.find(clients, context, CLIENT_NAME).isEmpty());
    }

    @Test
    public void testClientOnRequestNotAllowedList() {
        final MockIndirectClient client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final MockIndirectClient client2 =
            new MockIndirectClient(CLIENT_NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create().addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, NAME);
        assertTrue(finder.find(clients, context, CLIENT_NAME + "," + FAKE_VALUE).isEmpty());
    }

    @Test
    public void testNoClientOnRequest() {
        final MockIndirectClient client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final MockIndirectClient client2 =
            new MockIndirectClient(CLIENT_NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        final List<Client> currentClients = finder.find(clients, context, CLIENT_NAME);
        assertEquals(1, currentClients.size());
        assertEquals(client2, currentClients.get(0));
    }

    @Test
    public void testNoClientOnRequestBadDefaultClient() {
        final MockIndirectClient client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final MockIndirectClient client2 =
            new MockIndirectClient(CLIENT_NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        assertTrue(finder.find(clients, context, FAKE_VALUE).isEmpty());
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
        final MockIndirectClient client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final MockIndirectClient client2 =
            new MockIndirectClient(CLIENT_NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final Clients clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        final List<Client> currentClients = finder.find(clients, context, names);
        assertEquals(2, currentClients.size());
        assertEquals(client2, currentClients.get(0));
        assertEquals(client1, currentClients.get(1));
    }

    @Test
    public void testDefaultSecurityClients() {
        final MockIndirectClient client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final MockIndirectClient client2 =
            new MockIndirectClient(CLIENT_NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final Clients clients = new Clients(client1, client2);
        clients.setDefaultSecurityClients(CLIENT_NAME);
        final List<Client> result = finder.find(clients, MockWebContext.create(), null);
        assertEquals(1, result.size());
        assertEquals(client2, result.get(0));
    }

    @Test
    public void testOneClientAsDefault() {
        final MockIndirectClient client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final Clients clients = new Clients(client1);
        final List<Client> result = finder.find(clients, MockWebContext.create(), null);
        assertEquals(1, result.size());
        assertEquals(client1, result.get(0));
    }

    @Test
    public void testBlankClientRequested() {
        final MockIndirectClient client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), (Credentials) null, new CommonProfile());
        final Clients clients = new Clients(client1);
        final List<Client> result = finder.find(clients, MockWebContext.create(), "");
        assertEquals(0, result.size());
    }
}
