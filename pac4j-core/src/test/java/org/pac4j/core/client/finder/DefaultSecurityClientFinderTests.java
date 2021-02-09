package org.pac4j.core.client.finder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests {@link DefaultSecurityClientFinder}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@RunWith(Parameterized.class)
public final class DefaultSecurityClientFinderTests implements TestsConstants, Pac4jConstants {

    private DefaultSecurityClientFinder finder;

    @Parameterized.Parameter
    public String clientNameParameter;

    @Parameterized.Parameters
    public static Object[] data() {
        return new Object[] {null, "custom"};
    }

    @Before
    public void setUp() {
        finder = new DefaultSecurityClientFinder();
        if (clientNameParameter != null) {
            finder.setClientNameParameter(clientNameParameter);
        }
    }

    @Test
    public void testBlankClientName() {
        final var currentClients = finder.find(new Clients(), MockWebContext.create(), "  ");
        assertEquals(0, currentClients.size());
    }

    @Test
    public void testClientOnRequestAllowed() {
        internalTestClientOnRequestAllowedList(NAME, NAME);
    }

    @Test
    public void testBadClientOnRequest() {
        final var client =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var clients = new Clients(client);
        final WebContext context = MockWebContext.create().addRequestParameter(getClientNameParameter(), FAKE_VALUE);
        assertTrue(finder.find(clients, context, NAME).isEmpty());
    }

    protected String getClientNameParameter() {
        if (clientNameParameter != null) {
            return clientNameParameter;
        } else {
            return DEFAULT_FORCE_CLIENT_PARAMETER;
        }
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
        final var client =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var clients = new Clients(client);
        final WebContext context = MockWebContext.create().addRequestParameter(getClientNameParameter(), parameterName);
        final var currentClients = finder.find(clients, context, names);
        assertEquals(1, currentClients.size());
        assertEquals(client, currentClients.get(0));
    }

    @Test
    public void testClientOnRequestNotAllowed() {
        final var client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var client2 =
            new MockIndirectClient(MY_CLIENT_NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create().addRequestParameter(getClientNameParameter(), NAME);
        assertTrue(finder.find(clients, context, MY_CLIENT_NAME).isEmpty());
    }

    @Test
    public void testClientOnRequestNotAllowedList() {
        final var client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var client2 =
            new MockIndirectClient(MY_CLIENT_NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create().addRequestParameter(getClientNameParameter(), NAME);
        assertTrue(finder.find(clients, context, MY_CLIENT_NAME + "," + FAKE_VALUE).isEmpty());
    }

    @Test
    public void testNoClientOnRequest() {
        final var client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var client2 =
            new MockIndirectClient(MY_CLIENT_NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        final var currentClients = finder.find(clients, context, MY_CLIENT_NAME);
        assertEquals(1, currentClients.size());
        assertEquals(client2, currentClients.get(0));
    }

    @Test
    public void testNoClientOnRequestBadDefaultClient() {
        final var client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var client2 =
            new MockIndirectClient(MY_CLIENT_NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        assertTrue(finder.find(clients, context, FAKE_VALUE).isEmpty());
    }

    @Test
    public void testNoClientOnRequestList() {
        internalTestNoClientOnRequestList(MY_CLIENT_NAME + "," + NAME);
    }

    @Test
    public void testNoClientOnRequestListBlankSpaces() {
        internalTestNoClientOnRequestList(MY_CLIENT_NAME + " ," + NAME);
    }

    @Test
    public void testNoClientOnRequestListDifferentCase() {
        internalTestNoClientOnRequestList(MY_CLIENT_NAME.toUpperCase() + "," + NAME);
    }

    @Test
    public void testNoClientOnRequestListUppercase() {
        internalTestNoClientOnRequestList(MY_CLIENT_NAME.toUpperCase() + "," + NAME);
    }

    private void internalTestNoClientOnRequestList(final String names) {
        final var client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var client2 =
            new MockIndirectClient(MY_CLIENT_NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        final var currentClients = finder.find(clients, context, names);
        assertEquals(2, currentClients.size());
        assertEquals(client2, currentClients.get(0));
        assertEquals(client1, currentClients.get(1));
    }

    @Test
    public void testDefaultSecurityClients() {
        final var client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var client2 =
            new MockIndirectClient(MY_CLIENT_NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var clients = new Clients(client1, client2);
        clients.setDefaultSecurityClients(MY_CLIENT_NAME);
        final var result = finder.find(clients, MockWebContext.create(), null);
        assertEquals(1, result.size());
        assertEquals(client2, result.get(0));
    }

    @Test
    public void testOneClientAsDefault() {
        final var client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var clients = new Clients(client1);
        final var result = finder.find(clients, MockWebContext.create(), null);
        assertEquals(1, result.size());
        assertEquals(client1, result.get(0));
    }

    @Test
    public void testBlankClientRequested() {
        final var client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var clients = new Clients(client1);
        final var result = finder.find(clients, MockWebContext.create(), "");
        assertEquals(0, result.size());
    }
}
