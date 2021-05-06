package org.pac4j.core.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.callback.NoParameterCallbackUrlResolver;
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
        return new MockIndirectClient("FacebookClient", new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
    }

    private MockIndirectClient newYahooClient() {
        return new MockIndirectClient("YahooClient", new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
    }

    @Test
    public void testMissingClient() {
        final var clients = new Clients();
        clients.setCallbackUrl(CALLBACK_URL);
        TestsHelper.expectException(() -> clients.setClients((List<Client>) null), TechnicalException.class, "clients cannot be null");
    }

    @Test
    public void testNoValuesSet() {
        var facebookClient = newFacebookClient();
        final var clients = new Clients(facebookClient);
        clients.findAllClients();
        assertNull(facebookClient.getCallbackUrl());
        assertNull(facebookClient.getUrlResolver());
        assertNull(facebookClient.getCallbackUrlResolver());
        assertNull(facebookClient.getAjaxRequestResolver());
        assertEquals(0, facebookClient.getAuthorizationGenerators().size());
    }

    @Test
    public void testValuesSet() {
        var facebookClient = newFacebookClient();
        final var clients = new Clients(facebookClient);
        final AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();
        final UrlResolver urlResolver = new DefaultUrlResolver();
        final CallbackUrlResolver callbackUrlResolver = new QueryParameterCallbackUrlResolver();
        final AuthorizationGenerator authorizationGenerator = (context, store, profile) -> Optional.of(profile);
        clients.setCallbackUrl(CALLBACK_URL);
        clients.setAjaxRequestResolver(ajaxRequestResolver);
        clients.setUrlResolver(urlResolver);
        clients.setCallbackUrlResolver(callbackUrlResolver);
        clients.addAuthorizationGenerator(authorizationGenerator);
        clients.findAllClients();
        assertEquals(CALLBACK_URL, facebookClient.getCallbackUrl());
        assertEquals(urlResolver, facebookClient.getUrlResolver());
        assertEquals(callbackUrlResolver, facebookClient.getCallbackUrlResolver());
        assertEquals(ajaxRequestResolver, facebookClient.getAjaxRequestResolver());
        assertEquals(authorizationGenerator, facebookClient.getAuthorizationGenerators().get(0));
    }

    @Test
    public void testAllClients() {
        final var facebookClient = newFacebookClient();
        final var yahooClient = newYahooClient();
        final List<Client> clients = new ArrayList<>();
        clients.add(facebookClient);
        clients.add(yahooClient);
        final var clientsGroup = new Clients();
        clientsGroup.setClients(clients);
        clientsGroup.setCallbackUrl(CALLBACK_URL);
        final var clients2 = clientsGroup.findAllClients();
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
        final var facebookClient = newFacebookClient();
        final var fakeClient = new MockDirectClient(NAME, Optional.empty(), null);
        final Clients clients;
        if (fakeFirst) {
            clients = new Clients(CALLBACK_URL, fakeClient, facebookClient);
        } else {
            clients = new Clients(CALLBACK_URL, facebookClient, fakeClient);
        }
        assertEquals(facebookClient, clients.findClient(MockIndirectClient.class).get());
        assertEquals(fakeClient, clients.findClient(MockDirectClient.class).get());
    }

    @Test
    public void rejectSameName() {
        final var client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var client2 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var clients = new Clients(CALLBACK_URL, client1, client2);
        TestsHelper.expectException(() -> clients.findAllClients(),
            TechnicalException.class, "Duplicate name in clients: name");
    }

    @Test
    public void rejectSameNameOnAddingNewClient() {
        final var client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var clients = new Clients(CALLBACK_URL, client1);
        final var client2 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        clients.getClients().add(client2);
        TestsHelper.expectException(() -> clients.findClient(NAME),
            TechnicalException.class, "Duplicate name in clients: name");
    }

    @Test
    public void rejectSameNameDifferentCase() {
        final var client1 =
            new MockIndirectClient(NAME, new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var client2 =
            new MockIndirectClient(NAME.toUpperCase(), new FoundAction(LOGIN_URL), Optional.empty(), new CommonProfile());
        final var clients = new Clients(CALLBACK_URL, client1, client2);
        TestsHelper.expectException(() -> clients.findAllClients(),
            TechnicalException.class, "Duplicate name in clients: NAME");
    }

    @Test
    public void testFindByName() {
        final var facebookClient = newFacebookClient();
        final var yahooClient = newYahooClient();
        final var clients = new Clients(facebookClient, yahooClient);
        assertNotNull(clients.findClient("FacebookClient"));
    }

    @Test
    public void testFindByNameCase() {
        final var facebookClient = newFacebookClient();
        final var yahooClient = newYahooClient();
        final var clients = new Clients(facebookClient, yahooClient);
        assertNotNull(clients.findClient("FACEBOOKclient"));
    }

    @Test
    public void testFindByNameBlankSpaces() {
        final var facebookClient = newFacebookClient();
        final var yahooClient = newYahooClient();
        final var clients = new Clients(facebookClient, yahooClient);
        assertNotNull(clients.findClient(" FacebookClient          "));
    }

    @Test
    public void testAddClient() {
        final var facebookClient = newFacebookClient();
        final var yahooClient = newYahooClient();
        final var clients = new Clients(CALLBACK_URL, facebookClient);
        clients.findAllClients();
        final List<Client> list = new ArrayList<>();
        list.add(facebookClient);
        list.add(yahooClient);
        clients.setClients(list);
        clients.setCallbackUrlResolver(new NoParameterCallbackUrlResolver());
        final var yclient = (IndirectClient) clients.findClient("YahooClient").get();
        assertTrue(yclient.getCallbackUrlResolver() instanceof NoParameterCallbackUrlResolver);
        final var fclient = (IndirectClient) clients.findClient("FacebookClient").get();
        assertTrue(fclient.getCallbackUrlResolver() instanceof NoParameterCallbackUrlResolver);
    }
}
