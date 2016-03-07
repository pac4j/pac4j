package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

import static org.junit.Assert.*;

/**
 * This class tests the {@link HeaderClient} class.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class HeaderClientTests implements TestsConstants {

    @Test
    public void testMissingTokendAuthenticator() {
        final HeaderClient client = new HeaderClient(VALUE, null);
        TestsHelper.initShouldFail(client, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final HeaderClient client = new HeaderClient(NAME, new SimpleTestTokenAuthenticator());
        client.setProfileCreator(null);
        TestsHelper.initShouldFail(client, "profileCreator cannot be null");
    }

    @Test
    public void testBadAuthenticatorType() {
        final HeaderClient client = new HeaderClient(NAME, new SimpleTestUsernamePasswordAuthenticator());
        TestsHelper.initShouldFail(client, "Unsupported authenticator type: class org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final HeaderClient client = new HeaderClient(HEADER_NAME, new SimpleTestTokenAuthenticator());
        client.init(null);
    }

    @Test
    public void testMissingHeaderName() {
        final HeaderClient client = new HeaderClient(null, new SimpleTestTokenAuthenticator());
        TestsHelper.initShouldFail(client, "headerName cannot be blank");
    }

    @Test
    public void testAuthentication() throws RequiresHttpAction {
        final HeaderClient client = new HeaderClient(HEADER_NAME, PREFIX_HEADER, new SimpleTestTokenAuthenticator());
        final MockWebContext context = MockWebContext.create();
        context.addRequestHeader(HEADER_NAME, PREFIX_HEADER + VALUE);
        final TokenCredentials credentials = client.getCredentials(context);
        final UserProfile profile = client.getUserProfile(credentials, context);
        assertEquals(VALUE, profile.getId());
    }
}
