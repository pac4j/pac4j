package org.pac4j.cas.client;

import org.junit.Test;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
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
        TestsHelper.initShouldFail(casClient, "casLoginUrl and casPrefixUrl cannot be both blank");
    }

    @Test
    public void testMissingSlashOnPrefixUrl() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        casClient.setCasPrefixUrl(PREFIX_URL_WITHOUT_SLASH);
        casClient.init(null);
        assertEquals(PREFIX_URL, casClient.getCasPrefixUrl());
    }

    @Test
    public void testInitPrefixUrl() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        assertEquals(null, casClient.getCasPrefixUrl());
        casClient.init(null);
        assertEquals(PREFIX_URL, casClient.getCasPrefixUrl());
    }

    @Test
    public void testInitLoginUrl() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasPrefixUrl(PREFIX_URL);
        assertEquals(null, casClient.getCasLoginUrl());
        casClient.init(null);
        assertEquals(LOGIN_URL, casClient.getCasLoginUrl());
    }

    @Test
    public void testInitCallbackUrlResolver() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CASBACK);
        casClient.setCasPrefixUrl(CAS);
        casClient.setCasLoginUrl(CAS + LOGIN);
        casClient.setCallbackUrlResolver((callbackUrl, context) -> HOST + callbackUrl);
        casClient.init(null);
        assertEquals(HOST + CAS + LOGIN, casClient.getCasLoginUrl());
        assertEquals(HOST + CAS + "/", casClient.getCasPrefixUrl());
    }

    @Test
    public void testRenew() throws RequiresHttpAction {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        MockWebContext context = MockWebContext.create();
		casClient.redirect(context);
        assertFalse(context.getResponseLocation().indexOf("renew=true") >= 0);
        casClient.setRenew(true);
        casClient.reinit(null);
        context = MockWebContext.create();
        casClient.redirect(context);
        assertTrue(context.getResponseLocation().indexOf("renew=true") >= 0);
    }

    @Test
    public void testGateway() throws RequiresHttpAction {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        final MockWebContext context = MockWebContext.create();
        casClient.redirect(context);
        assertFalse(context.getResponseLocation().indexOf("gateway=true") >= 0);
        casClient.setGateway(true);
        casClient.reinit(null);
        casClient.redirect(context);
        assertTrue(context.getResponseLocation().indexOf("gateway=true") >= 0);
        final CasCredentials credentials = casClient.getCredentials(context);
        assertNull(credentials);
    }

    @Test
    public void testLogout() {
        final String logoutRequest = "<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" ID=\"LR-1-B2b0CVRW5eSvPBZPsAVXdNPj7jee4SWjr9y\" Version=\"2.0\" IssueInstant=\"2012-12-19T15:30:55Z\"><saml:NameID xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">@NOT_USED@</saml:NameID><samlp:SessionIndex>ST-1-FUUhL26EgrkcD6I2Mry9-cas01.example.org</samlp:SessionIndex></samlp:LogoutRequest>";
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        casClient.init(null);
        final MockWebContext context = MockWebContext.create().addRequestParameter("logoutRequest", logoutRequest)
            .setRequestMethod("POST");
        try {
            casClient.getCredentials(context);
            fail("should throw RequiresHttpAction");
        } catch (final RequiresHttpAction e) {
            assertEquals(200, context.getResponseStatus());
            assertEquals("logout request : no credential returned", e.getMessage());
        }
    }

    @Test
    public void testInitUrlWithLoginString() {
    	final String testCasLoginUrl = "https://login.foo.bar/login/login";
    	final String testCasPrefixUrl = "https://login.foo.bar/login/";
    	final CasClient casClient = new CasClient();
    	casClient.setCasLoginUrl(testCasLoginUrl);
    	casClient.setCallbackUrl(CALLBACK_URL);
    	assertEquals(null, casClient.getCasPrefixUrl());
    	casClient.init(null);
    	assertEquals(testCasPrefixUrl, casClient.getCasPrefixUrl());
    }
}
