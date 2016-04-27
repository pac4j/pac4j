package org.pac4j.oauth.client;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

/**
 * Tests the OAuth clients.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class OAuthClientsTests implements TestsConstants {

    @Test
    public void testMissingFieldsFacebook() {
        final FacebookClient client = new FacebookClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setFields(null);
        TestsHelper.initShouldFail(client, "fields cannot be blank");
    }

    private Google2Client getGoogleClient() {
        final Google2Client google2Client = new Google2Client(KEY, SECRET);
        google2Client.setCallbackUrl(CALLBACK_URL);
        return google2Client;
    }

    @Test
    public void testMissingScopeGoogle() {
        final Google2Client client = getGoogleClient();
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be null");
    }

    @Test
    public void testDefaultScopeGoogle() throws HttpAction {
        getGoogleClient().redirect(MockWebContext.create());
    }

    @Test
    public void testMissingFieldsOk() {
        final OkClient client = new OkClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setPublicKey(null);
        TestsHelper.initShouldFail(client, "publicKey cannot be blank");
    }

    private LinkedIn2Client getLinkedInClient() {
        final LinkedIn2Client client = new LinkedIn2Client(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    public void testMissingScopeLinkedIn() {
        final LinkedIn2Client client = getLinkedInClient();
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be blank");
    }

    @Test
    public void testMissingFieldsLinkedIn() {
        final LinkedIn2Client client = getLinkedInClient();
        client.setFields(null);
        TestsHelper.initShouldFail(client, "fields cannot be blank");
    }

    @Test
    public void testMissingFieldsPaypal() {
        final PayPalClient client = new PayPalClient(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be blank");
    }
}
