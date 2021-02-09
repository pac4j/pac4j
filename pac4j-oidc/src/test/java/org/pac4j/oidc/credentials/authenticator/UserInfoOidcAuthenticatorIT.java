package org.pac4j.oidc.credentials.authenticator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Answers;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.http.test.tools.ServerResponse;
import org.pac4j.http.test.tools.WebServer;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfile;

import fi.iki.elonen.NanoHTTPD;

/**
 * Tests {@link UserInfoOidcAuthenticator}.
 *
 * @author Rakesh Sarangi
 * @since 3.5.0
 */
public class UserInfoOidcAuthenticatorIT implements TestsConstants {

    private static final int PORT = 8088;

    @BeforeClass
    public static void setUp() {
        final var webServer = new WebServer(PORT)
            .defineResponse("ok", new ServerResponse(NanoHTTPD.Response.Status.OK, "application/json",
                String.format("{%n" +
                    "    \"sub\": \"%s\",%n" +
                    "    \"name\": \"%s\",%n" +
                    "    \"preferred_username\": \"%s\"%n" +
                    "}", ID, GOOD_USERNAME, USERNAME)))
            .defineResponse("notfound", new ServerResponse(NanoHTTPD.Response.Status.NOT_FOUND, "plain/text", "Not found"));
        webServer.start();
    }

    @Test
    public void testOkay() throws URISyntaxException {
        final var configuration = mock(OidcConfiguration.class, Answers.RETURNS_DEEP_STUBS);
        when(configuration.findProviderMetadata().getUserInfoEndpointURI()).thenReturn(new URI("http://localhost:" + PORT + "?r=ok"));
        final var authenticator = new UserInfoOidcAuthenticator(configuration);
        final var credentials = getCredentials();

        authenticator.validate(credentials, MockWebContext.create(), new MockSessionStore());

        final var profile = (OidcProfile) credentials.getUserProfile();
        assertEquals(GOOD_USERNAME, profile.getDisplayName());
        assertEquals(USERNAME, profile.getUsername());
        assertEquals(credentials.getToken(), profile.getAccessToken().getValue());
    }

    @Test(expected = TechnicalException.class)
    public void testNotFound() throws URISyntaxException {
        final var configuration = mock(OidcConfiguration.class, Answers.RETURNS_DEEP_STUBS);
        when(configuration.findProviderMetadata().getUserInfoEndpointURI()).thenReturn(new URI("http://localhost:" + PORT + "?r=notfound"));
        final var authenticator = new UserInfoOidcAuthenticator(configuration);
        final var credentials = getCredentials();

        authenticator.validate(credentials, MockWebContext.create(), new MockSessionStore());
    }

    private TokenCredentials getCredentials() {
        final var token = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..NTvhJXwZ_sN4zYBK.exyLJWkOclCVcffz58CE-"
            + "3XWWV24aYyGWR5HVrfm4HLQi1xgmwglLlEIiFlOSTOSZ_LeAwl2Z3VFh-5EidocjwGkAPGQA_4_KCLbK8Im7M25ZZvDzCJ1kKN1JrDIIrBWCcuI4Mbw0O"
            + "_YGb8TfIECPkpeG7wEgBG30sb1kH-F_vg9yjYfB4MiJCSFmY7cRqN9-9O23tz3wYv3b-eJh5ACr2CGSVNj2KcMsOMJ6bbALgz6pzQTIWk_"
            + "fhcE9QSfaSY7RuZ8cRTV-UTjYgZk1gbd1LskgchS.ijMQmfPlObJv7oaPG8LCEg";
        return new TokenCredentials(token);
    }
}
