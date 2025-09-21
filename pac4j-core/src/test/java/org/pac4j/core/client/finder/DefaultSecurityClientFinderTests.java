package org.pac4j.core.client.finder;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link DefaultSecurityClientFinder}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultSecurityClientFinderTests implements TestsConstants, Pac4jConstants {

    private DefaultSecurityClientFinder finder;

    @BeforeEach
    public void setUp() {
        finder = new DefaultSecurityClientFinder();
    }

    private void setupFinderWithParameter(String clientNameParameter) {
        finder = new DefaultSecurityClientFinder();
        if (clientNameParameter != null) {
            finder.setClientNameParameter(clientNameParameter);
        }
    }

    @Test
    @DisplayName("Should return empty list when client name is blank")
    public void testBlankClientName() {
        val currentClients = finder.find(new Clients(), MockWebContext.create(), "  ");
        assertTrue(currentClients.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {DEFAULT_FORCE_CLIENT_PARAMETER, "custom"})
    @DisplayName("Should find client when client name is allowed on request")
    public void testClientOnRequestAllowed(String clientNameParameter) {
        setupFinderWithParameter(clientNameParameter);
        internalTestClientOnRequestAllowedList(NAME, NAME, clientNameParameter);
    }

    @ParameterizedTest
    @ValueSource(strings = {DEFAULT_FORCE_CLIENT_PARAMETER, "custom"})
    @DisplayName("Should return empty list when bad client is requested")
    public void testBadClientOnRequest(String clientNameParameter) {
        setupFinderWithParameter(clientNameParameter);
        val client =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val clients = new Clients(client);
        final WebContext context = MockWebContext.create().addRequestParameter(getClientNameParameter(clientNameParameter), FAKE_VALUE);
        assertTrue(finder.find(clients, context, NAME).isEmpty());
    }

    private String getClientNameParameter(String clientNameParameter) {
        return Objects.requireNonNullElse(clientNameParameter, DEFAULT_FORCE_CLIENT_PARAMETER);
    }

    @ParameterizedTest
    @ValueSource(strings = {DEFAULT_FORCE_CLIENT_PARAMETER, "custom"})
    @DisplayName("Should find client when client name is in allowed list on request")
    public void testClientOnRequestAllowedList(String clientNameParameter) {
        setupFinderWithParameter(clientNameParameter);
        internalTestClientOnRequestAllowedList(NAME, FAKE_VALUE + "," + NAME, clientNameParameter);
    }

    @ParameterizedTest
    @ValueSource(strings = {DEFAULT_FORCE_CLIENT_PARAMETER, "custom"})
    @DisplayName("Should find client with case insensitive and trimmed names in allowed list")
    public void testClientOnRequestAllowedListCaseTrim(String clientNameParameter) {
        setupFinderWithParameter(clientNameParameter);
        internalTestClientOnRequestAllowedList("NaMe  ", FAKE_VALUE.toUpperCase() + "  ,       nAmE", clientNameParameter);
    }

    private void internalTestClientOnRequestAllowedList(final String parameterName, final String names, String clientNameParameter) {
        val client =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val clients = new Clients(client);
        final WebContext context = MockWebContext.create().addRequestParameter(getClientNameParameter(clientNameParameter), parameterName);
        val currentClients = finder.find(clients, context, names);
        assertEquals(1, currentClients.size());
        assertEquals(client, currentClients.get(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {DEFAULT_FORCE_CLIENT_PARAMETER, "custom"})
    @DisplayName("Should return empty list when requested client is not in allowed list")
    public void testClientOnRequestNotAllowed(String clientNameParameter) {
        setupFinderWithParameter(clientNameParameter);
        val client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val client2 =
            new MockIndirectClient(MY_CLIENT_NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create().addRequestParameter(getClientNameParameter(clientNameParameter), NAME);
        assertTrue(finder.find(clients, context, MY_CLIENT_NAME).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {DEFAULT_FORCE_CLIENT_PARAMETER, "custom"})
    @DisplayName("Should return empty list when requested client is not in allowed list with multiple clients")
    public void testClientOnRequestNotAllowedList(String clientNameParameter) {
        setupFinderWithParameter(clientNameParameter);
        val client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val client2 =
            new MockIndirectClient(MY_CLIENT_NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create().addRequestParameter(getClientNameParameter(clientNameParameter), NAME);
        assertTrue(finder.find(clients, context, MY_CLIENT_NAME + "," + FAKE_VALUE).isEmpty());
    }

    @Test
    @DisplayName("Should find default client when no client is specified on request")
    public void testNoClientOnRequest() {
        val client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val client2 =
            new MockIndirectClient(MY_CLIENT_NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        val currentClients = finder.find(clients, context, MY_CLIENT_NAME);
        assertEquals(1, currentClients.size());
        assertEquals(client2, currentClients.get(0));
    }

    @Test
    @DisplayName("Should return empty list when default client name is invalid")
    public void testNoClientOnRequestBadDefaultClient() {
        val client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val client2 =
            new MockIndirectClient(MY_CLIENT_NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        assertTrue(finder.find(clients, context, FAKE_VALUE).isEmpty());
    }

    @Test
    @DisplayName("Should find multiple clients when no client is specified on request")
    public void testNoClientOnRequestList() {
        internalTestNoClientOnRequestList(MY_CLIENT_NAME + "," + NAME);
    }

    @Test
    @DisplayName("Should find multiple clients with blank spaces in names when no client is specified on request")
    public void testNoClientOnRequestListBlankSpaces() {
        internalTestNoClientOnRequestList(MY_CLIENT_NAME + " ," + NAME);
    }

    @Test
    @DisplayName("Should find multiple clients with different case when no client is specified on request")
    public void testNoClientOnRequestListDifferentCase() {
        internalTestNoClientOnRequestList(MY_CLIENT_NAME.toUpperCase() + "," + NAME);
    }

    @Test
    @DisplayName("Should find multiple clients with uppercase names when no client is specified on request")
    public void testNoClientOnRequestListUppercase() {
        internalTestNoClientOnRequestList(MY_CLIENT_NAME.toUpperCase() + "," + NAME);
    }

    private void internalTestNoClientOnRequestList(final String names) {
        val client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val client2 =
            new MockIndirectClient(MY_CLIENT_NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val clients = new Clients(client1, client2);
        final WebContext context = MockWebContext.create();
        val currentClients = finder.find(clients, context, names);
        assertEquals(2, currentClients.size());
        assertEquals(client2, currentClients.get(0));
        assertEquals(client1, currentClients.get(1));
    }

    @Test
    @DisplayName("Should find default security client when set")
    public void testDefaultSecurityClients() {
        val client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val client2 =
            new MockIndirectClient(MY_CLIENT_NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val clients = new Clients(client1, client2);
        clients.setDefaultSecurityClients(MY_CLIENT_NAME);
        val result = finder.find(clients, MockWebContext.create(), null);
        assertEquals(1, result.size());
        assertEquals(client2, result.get(0));
    }

    @Test
    @DisplayName("Should find single client as default when only one client exists")
    public void testOneClientAsDefault() {
        val client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val clients = new Clients(client1);
        val result = finder.find(clients, MockWebContext.create(), null);
        assertEquals(1, result.size());
        assertEquals(client1, result.get(0));
    }

    @Test
    @DisplayName("Should return empty list when blank client is requested")
    public void testBlankClientRequested() {
        val client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        val clients = new Clients(client1);
        val result = finder.find(clients, MockWebContext.create(), Pac4jConstants.EMPTY_STRING);
        assertTrue(result.isEmpty());
    }
}
