package org.pac4j.http.credentials.authenticator;

import fi.iki.elonen.NanoHTTPD;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.profile.RestProfile;
import org.pac4j.http.test.tools.ServerResponse;
import org.pac4j.http.test.tools.WebServer;

import static org.junit.Assert.*;

/**
 * Tests {@link RestAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 2.1.0
 */
public final class RestAuthenticatorIT implements TestsConstants {

    private static final int PORT = 8088;

    @BeforeClass
    public static void setUp() {
        final WebServer webServer = new WebServer(PORT)
            .defineResponse("ok", new ServerResponse(NanoHTTPD.Response.Status.OK, "application/json",
                "{ 'id': '" + ID + "', roles: [\"" + ROLE + "\"] }"))
            .defineResponse("notfound", new ServerResponse(NanoHTTPD.Response.Status.NOT_FOUND, "plain/text", "Not found"))
            .defineResponse("pe", new ServerResponse(NanoHTTPD.Response.Status.OK, "plain/text", "bad"));
        webServer.start();
    }

    @Test
    public void testProfileOk() {
        final RestAuthenticator authenticator = new RestAuthenticator("http://localhost:" + PORT + "?r=ok");
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
        authenticator.validate(credentials, MockWebContext.create());
        final RestProfile profile = (RestProfile) credentials.getUserProfile();
        assertNotNull(profile);
        assertEquals(ID, profile.getId());
        assertEquals(1, profile.getRoles().size());
        assertEquals(ROLE, profile.getRoles().iterator().next());
    }

    @Test
    public void testNotFound() {
        final RestAuthenticator authenticator = new RestAuthenticator("http://localhost:" + PORT + "?r=notfound");
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
        authenticator.validate(credentials, MockWebContext.create());
        final RestProfile profile = (RestProfile) credentials.getUserProfile();
        assertNull(profile);
    }

    @Test
    public void testParsingError() {
        final RestAuthenticator authenticator = new RestAuthenticator("http://localhost:" + PORT + "?r=pe");
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
        TestsHelper.expectException(() -> authenticator.validate(credentials, MockWebContext.create()), TechnicalException.class,
            "com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'bad': was expecting ('true', 'false' or 'null')\n"
            + " at [Source: (String)\"bad\"; line: 1, column: 7]");
    }

    @Test
    public void testHttps() {
        final RestAuthenticator authenticator = new RestAuthenticator("https://www.google.com");
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
        authenticator.validate(credentials, MockWebContext.create());
    }
}
