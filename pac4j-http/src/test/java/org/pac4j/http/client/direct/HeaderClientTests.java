package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Optional;

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
        TestsHelper.expectException(() -> client.getCredentials(MockWebContext.create()), TechnicalException.class,
            "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final HeaderClient client = new HeaderClient(NAME, new SimpleTestTokenAuthenticator());
        client.setProfileCreator(null);
        TestsHelper.expectException(() -> client.getUserProfile(new TokenCredentials(TOKEN),
                MockWebContext.create()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final HeaderClient client = new HeaderClient(HEADER_NAME, new SimpleTestTokenAuthenticator());
        client.init();
    }

    @Test
    public void testMissingHeaderName() {
        final HeaderClient client = new HeaderClient(null, new SimpleTestTokenAuthenticator());
        TestsHelper.initShouldFail(client, "headerName cannot be blank");
    }

    @Test
    public void testAuthentication() {
        final HeaderClient client = new HeaderClient(HEADER_NAME, PREFIX_HEADER, new SimpleTestTokenAuthenticator());
        final MockWebContext context = MockWebContext.create();
        context.addRequestHeader(HEADER_NAME, PREFIX_HEADER + VALUE);
        final TokenCredentials credentials = client.getCredentials(context);
        @SuppressWarnings("unchecked")
		Map<HeaderClient, TokenCredentials> csm = (Map<HeaderClient, TokenCredentials>) context.getRequestAttribute(BaseClient.CREDENTIALS_SUPPLIED_MAP);
        assertNotNull(csm);
        TokenCredentials tc = csm.get(client);
        assertEquals(VALUE, tc.getToken());
        final CommonProfile profile = client.getUserProfile(credentials, context);
        assertEquals(VALUE, profile.getId());
    }
}
