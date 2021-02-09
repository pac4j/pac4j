package org.pac4j.oauth.client;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.util.generator.StaticValueGenerator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * This class tests the {@link OAuth20Client} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuth20ClientTests implements TestsConstants {

    private OAuth20Client getClient() {
        final var client = new GitHubClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    public void testState() throws MalformedURLException {
        var client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.getConfiguration().setStateGenerator(new StaticValueGenerator("OK"));
        final var action = (FoundAction) client.getRedirectionAction(MockWebContext.create(), new MockSessionStore()).get();
        var url = new URL(action.getLocation());
        assertTrue(url.getQuery().contains("state=OK"));
    }

    @Test
    public void testSetState() throws MalformedURLException {
        var client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.getConfiguration().setStateGenerator(new StaticValueGenerator("oldstate"));
        final var mockWebContext = MockWebContext.create();
        var action = (FoundAction) client.getRedirectionAction(mockWebContext, new MockSessionStore()).get();
        var url = new URL(action.getLocation());
        final var stringMap = TestsHelper.splitQuery(url);
        assertEquals(stringMap.get("state"), "oldstate");
        action = (FoundAction) client.getRedirectionAction(mockWebContext, new MockSessionStore()).get();
        var url2 = new URL(action.getLocation());
        final var stringMap2 = TestsHelper.splitQuery(url2);
        assertEquals(stringMap2.get("state"), "oldstate");
    }

    @Test
    public void testStateRandom() throws MalformedURLException {
        OAuth20Client client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        var action = (FoundAction) client.getRedirectionAction(MockWebContext.create(), new MockSessionStore()).get();
        var url = new URL(action.getLocation());
        final var stringMap = TestsHelper.splitQuery(url);
        assertNotNull(stringMap.get("state"));

        action = (FoundAction) client.getRedirectionAction(MockWebContext.create(), new MockSessionStore()).get();
        var url2 = new URL(action.getLocation());
        final var stringMap2 = TestsHelper.splitQuery(url2);
        assertNotNull(stringMap2.get("state"));
        assertNotEquals(stringMap.get("state"), stringMap2.get("state"));
    }

    @Test
    public void testGetRedirectionGithub() {
        final var action = (FoundAction) getClient().getRedirectionAction(MockWebContext.create(), new MockSessionStore()).get();
        final var url = action.getLocation();
        assertTrue(url != null && !url.isEmpty());
    }

    @Test
    public void testDefaultName20() {
        final OAuth20Client client = new FacebookClient();
        assertEquals("FacebookClient", client.getName());
    }

    @Test
    public void testDefinedName() {
        final OAuth20Client client = new FacebookClient();
        client.setName(TYPE);
        assertEquals(TYPE, client.getName());
    }

    @Test
    public void testMissingKey() {
        final var client = getClient();
        client.setKey(null);
        TestsHelper.expectException(() -> client.getRedirectionAction(MockWebContext.create(), new MockSessionStore()),
            TechnicalException.class, "key cannot be blank");
    }

    @Test
    public void testMissingSecret() {
        final var client = getClient();
        client.setSecret(null);
        TestsHelper.expectException(() -> client.getRedirectionAction(MockWebContext.create(), new MockSessionStore()),
            TechnicalException.class, "secret cannot be blank");
    }

    @Test
    public void testMissingFieldsFacebook() {
        final var client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setFields(null);
        TestsHelper.initShouldFail(client, "fields cannot be blank");
    }

    private Google2Client getGoogleClient() {
        final var google2Client = new Google2Client(KEY, SECRET);
        google2Client.setCallbackUrl(CALLBACK_URL);
        return google2Client;
    }

    @Test
    public void testMissingScopeGoogle() {
        final var client = getGoogleClient();
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be null");
    }

    @Test
    public void testDefaultScopeGoogle() {
        getGoogleClient().getRedirectionAction(MockWebContext.create(), new MockSessionStore());
    }

    @Test
    public void testMissingFieldsOk() {
        final var client = new OkClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setPublicKey(null);
        TestsHelper.initShouldFail(client, "publicKey cannot be blank");
    }

    private LinkedIn2Client getLinkedInClient() {
        final var client = new LinkedIn2Client(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    public void testMissingScopeLinkedIn() {
        final var client = getLinkedInClient();
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be blank");
    }

    @Test
    public void testMissingFieldsPaypal() {
        final var client = new PayPalClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be blank");
    }
}
