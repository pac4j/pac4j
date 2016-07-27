package org.pac4j.cas.client;

import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.*;

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

    @Test
    public void testMissingCasUrls() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(casClient, "loginUrl and prefixUrl cannot be both blank");
    }

    @Test
    public void testMissingSlashOnPrefixUrl() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setPrefixUrl(PREFIX_URL_WITHOUT_SLASH);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.init(null);
        assertEquals(PREFIX_URL, configuration.getPrefixUrl());
    }

    @Test
    public void testInitPrefixUrl() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        assertEquals(null, configuration.getPrefixUrl());
        casClient.init(null);
        assertEquals(PREFIX_URL, configuration.getPrefixUrl());
    }

    @Test
    public void testInitLoginUrl() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setPrefixUrl(PREFIX_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        assertEquals(null, configuration.getLoginUrl());
        casClient.init(null);
        assertEquals(LOGIN_URL, configuration.getLoginUrl());
    }

    @Test
    public void testInitCallbackUrlResolver() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setPrefixUrl(CAS);
        configuration.setLoginUrl(CAS + LOGIN);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CASBACK);
        casClient.setCallbackUrlResolver((callbackUrl, context) -> HOST + callbackUrl);
        casClient.init(null);
        assertEquals(HOST + CAS + LOGIN, configuration.getLoginUrl());
        assertEquals(HOST + CAS + "/", configuration.getPrefixUrl());
    }

    @Test
    public void testRenew() throws HttpAction {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        MockWebContext context = MockWebContext.create();
		casClient.redirect(context);
        assertFalse(context.getResponseLocation().indexOf("renew=true") >= 0);
        configuration.setRenew(true);
        casClient.reinit(null);
        context = MockWebContext.create();
        casClient.redirect(context);
        assertTrue(context.getResponseLocation().indexOf("renew=true") >= 0);
    }

    @Test
    public void testGateway() throws HttpAction {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        casClient.redirect(context);
        assertFalse(context.getResponseLocation().indexOf("gateway=true") >= 0);
        configuration.setGateway(true);
        casClient.reinit(null);
        casClient.redirect(context);
        assertTrue(context.getResponseLocation().indexOf("gateway=true") >= 0);
        final TokenCredentials credentials = casClient.getCredentials(context);
        assertNull(credentials);
    }

    @Test
    public void testLogout() {
        final String logoutRequest = "<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" ID=\"LR-1-B2b0CVRW5eSvPBZPsAVXdNPj7jee4SWjr9y\" Version=\"2.0\" IssueInstant=\"2012-12-19T15:30:55Z\"><saml:NameID xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">@NOT_USED@</saml:NameID><samlp:SessionIndex>ST-1-FUUhL26EgrkcD6I2Mry9-cas01.example.org</samlp:SessionIndex></samlp:LogoutRequest>";
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.init(null);
        final MockWebContext context = MockWebContext.create().addRequestParameter("logoutRequest", logoutRequest)
            .setRequestMethod("POST");
        TestsHelper.expectException(() -> casClient.getCredentials(context), HttpAction.class, "logout request: no credential returned");
        assertEquals(200, context.getResponseStatus());
    }

    @Test
    public void testInitUrlWithLoginString() {
    	final String testCasLoginUrl = "https://login.foo.bar/login/login";
    	final String testCasPrefixUrl = "https://login.foo.bar/login/";
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(testCasLoginUrl);
        final CasClient casClient = new CasClient(configuration);
    	casClient.setCallbackUrl(CALLBACK_URL);
    	assertEquals(null, configuration.getPrefixUrl());
    	casClient.init(null);
    	assertEquals(testCasPrefixUrl, configuration.getPrefixUrl());
    }
}
