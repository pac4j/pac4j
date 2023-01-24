package org.pac4j.cas.client;

import lombok.val;
import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.WithContentAction;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.Deflater;

import static org.junit.Assert.*;
import static org.pac4j.core.context.HttpConstants.FOUND;
import static org.pac4j.core.context.HttpConstants.HTTP_METHOD;

/**
 * This class tests the {@link CasClient} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CasClientTests implements TestsConstants {

    private static final String CAS = "/cas";
    private static final String CASBACK = "/casback";
    private static final String HOST = "protocol://myHost";
    private static final String LOGIN = "/login";
    private static final String PREFIX_URL = "http://myserver/";
    private static final String PREFIX_URL_WITHOUT_SLASH = "http://myserver";
    private static final String LOGOUT_MESSAGE = """
        <samlp:LogoutRequest xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol"
        ID="LR-1-B2b0CVRW5eSvPBZPsAVXdNPj7jee4SWjr9y" Version="2.0" IssueInstant="2012-12-19T15:30:55Z">
        <saml:NameID xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion">@NOT_USED@</saml:NameID><samlp:SessionIndex>"""
        + TICKET + "\"</samlp:SessionIndex></samlp:LogoutRequest>\";";

    @Test
    public void testMissingCasUrls() {
        val casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(casClient.getConfiguration(), "loginUrl, prefixUrl and restUrl cannot be all blank");
    }

    @Test
    public void testMissingSlashOnPrefixUrl() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setPrefixUrl(PREFIX_URL_WITHOUT_SLASH);
        val casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        configuration.init();
        assertEquals(PREFIX_URL, configuration.getPrefixUrl());
    }

    @Test
    public void testInitPrefixUrl() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        val casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        assertEquals(null, configuration.getPrefixUrl());
        configuration.init();
        assertEquals(PREFIX_URL, configuration.getPrefixUrl());
    }

    @Test
    public void testInitLoginUrl() {
        val configuration = new CasConfiguration();
        configuration.setPrefixUrl(PREFIX_URL);
        val casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        assertEquals(null, configuration.getLoginUrl());
        configuration.init();
        assertEquals(LOGIN_URL, configuration.getLoginUrl());
    }

    @Test
    public void testCallbackUrlResolver() {
        val configuration = new CasConfiguration();
        configuration.setPrefixUrl(CAS);
        configuration.setLoginUrl(CAS + LOGIN);
        val casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CASBACK);
        casClient.setUrlResolver((url, context) -> HOST + url);
        casClient.setCallbackUrlResolver(new CallbackUrlResolver() {
            @Override
            public String compute(final UrlResolver urlResolver, final String url, final String clientName, final WebContext context) {
                return null;
            }

            @Override
            public boolean matches(final String clientName, final WebContext context) {
                return false;
            }
        });
        casClient.init();
        assertEquals(HOST + CAS + LOGIN, configuration.computeFinalLoginUrl(null));
        assertEquals(HOST + CAS + "/", configuration.computeFinalPrefixUrl(null));
    }

    @Test
    public void testRenewMissing() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        val casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        val context = MockWebContext.create();
        val action = (FoundAction) casClient.getRedirectionAction(new CallContext(context, new MockSessionStore())).get();
        assertFalse(action.getLocation().indexOf("renew=true") >= 0);
    }

    @Test
    public void testRenew() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        val casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        configuration.setRenew(true);
        val context = MockWebContext.create();
        val action = (FoundAction) casClient.getRedirectionAction(new CallContext(context, new MockSessionStore())).get();
        assertTrue(action.getLocation().indexOf("renew=true") >= 0);
    }

    @Test
    public void testGatewayMissing() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        val casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        val context = MockWebContext.create();
        val action = (FoundAction) casClient.getRedirectionAction(new CallContext(context, new MockSessionStore())).get();
        assertFalse(action.getLocation().indexOf("gateway=true") >= 0);
    }

    @Test
    public void testGatewayOK() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        val casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        val context = MockWebContext.create();
        configuration.setGateway(true);
        val action = (FoundAction) casClient.getRedirectionAction(new CallContext(context, new MockSessionStore())).get();
        assertTrue(action.getLocation().indexOf("gateway=true") >= 0);
        val credentials = casClient.getCredentials(new CallContext(context, new MockSessionStore()));
        assertFalse(credentials.isPresent());
    }

    @Test
    public void testBackLogout() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        val casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.init();
        val context = MockWebContext.create()
            .addRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER, LOGOUT_MESSAGE)
            .setRequestMethod(HTTP_METHOD.POST.name());
        val ctx = new CallContext(context, new MockSessionStore());
        val credentials = casClient.getCredentials(ctx);
        val action = casClient.processLogout(ctx, credentials.get());
        assertEquals(204, action.getCode());
    }

    private String deflateAndBase64(final String data) {
        val deflater = new Deflater();
        deflater.setInput(data.getBytes(StandardCharsets.UTF_8));
        deflater.finish();
        val buffer = new byte[data.length()];
        val resultSize = deflater.deflate(buffer);
        val output = new byte[resultSize];
        System.arraycopy(buffer, 0, output, 0, resultSize);
        return Base64.getEncoder().encodeToString(output);
    }

    @Test
    public void testFrontLogout() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        val casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.init();
        val context = MockWebContext.create()
                .addRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER, deflateAndBase64(LOGOUT_MESSAGE))
                .setRequestMethod(HTTP_METHOD.GET.name());
        val ctx = new CallContext(context, new MockSessionStore());
        val credentials = casClient.getCredentials(ctx);
        val action = casClient.processLogout(ctx, credentials.get());
        assertEquals(200, action.getCode());
        assertEquals(Pac4jConstants.EMPTY_STRING, ((WithContentAction) action).getContent());
    }

    @Test
    public void testFrontLogoutWithRelayState() {
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        val casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.init();
        val context = MockWebContext.create()
                .addRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER, deflateAndBase64(LOGOUT_MESSAGE))
                .addRequestParameter(CasConfiguration.RELAY_STATE_PARAMETER, VALUE).setRequestMethod(HTTP_METHOD.GET.name());
        val ctx = new CallContext(context, new MockSessionStore());
        val credentials = casClient.getCredentials(ctx);
        val action = casClient.processLogout(ctx, credentials.get());
        assertEquals(FOUND, action.getCode());
    }

    @Test
    public void testInitUrlWithLoginString() {
        val testCasLoginUrl = "https://login.foo.bar/login/login";
        val testCasPrefixUrl = "https://login.foo.bar/login/";
        val configuration = new CasConfiguration();
        configuration.setLoginUrl(testCasLoginUrl);
        val casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        assertEquals(null, configuration.getPrefixUrl());
        configuration.init();
        assertEquals(testCasPrefixUrl, configuration.getPrefixUrl());
    }
}
