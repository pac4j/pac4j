package org.pac4j.cas.client;

import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.util.zip.Deflater;

import static org.junit.Assert.*;

import static org.pac4j.core.context.HttpConstants.*;

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
    private static final String LOGOUT_MESSAGE = "\"<samlp:LogoutRequest xmlns:samlp=\\\"urn:oasis:names:tc:SAML:2.0:protocol\\\" ID=\\\"LR-1-B2b0CVRW5eSvPBZPsAVXdNPj7jee4SWjr9y\\\" Version=\\\"2.0\\\" IssueInstant=\\\"2012-12-19T15:30:55Z\\\"><saml:NameID xmlns:saml=\\\"urn:oasis:names:tc:SAML:2.0:assertion\\\">@NOT_USED@</saml:NameID><samlp:SessionIndex>\" + TICKET + \"</samlp:SessionIndex></samlp:LogoutRequest>\";";

    @Test
    public void testMissingCasUrls() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(casClient, "loginUrl, prefixUrl and restUrl cannot be all blank");
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
    public void testCallbackUrlResolver() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setPrefixUrl(CAS);
        configuration.setLoginUrl(CAS + LOGIN);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CASBACK);
        casClient.setCallbackUrlResolver((callbackUrl, context) -> HOST + callbackUrl);
        casClient.init(null);
        assertEquals(HOST + CAS + LOGIN, configuration.computeFinalLoginUrl(null));
        assertEquals(HOST + CAS + "/", configuration.computeFinalPrefixUrl(null));
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
    public void testBackLogout() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.init(null);
        final MockWebContext context = MockWebContext.create().addRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER, LOGOUT_MESSAGE)
            .setRequestMethod(HTTP_METHOD.POST);
        TestsHelper.expectException(() -> casClient.getCredentials(context), HttpAction.class, "back logout request: no credential returned");
        assertEquals(200, context.getResponseStatus());
    }

    private String deflateAndBase64(final String data) {
        try {
            final Deflater deflater = new Deflater();
            deflater.setInput(data.getBytes(UTF8_ENCODING));
            deflater.finish();
            final byte[] buffer = new byte[data.length()];
            final int resultSize = deflater.deflate(buffer);
            final byte[] output = new byte[resultSize];
            System.arraycopy(buffer, 0, output, 0, resultSize);
            return DatatypeConverter.printBase64Binary(output);
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Cannot find encoding:" + UTF8_ENCODING, e);
        }
    }

    @Test
    public void testFrontLogout() throws HttpAction {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.init(null);
        final MockWebContext context = MockWebContext.create().addRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER, deflateAndBase64(LOGOUT_MESSAGE))
                .setRequestMethod(HTTP_METHOD.GET);
        assertNull(casClient.getCredentials(context));
    }

    @Test
    public void testFrontLogoutWithRelayState() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final CasClient casClient = new CasClient(configuration);
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.init(null);
        final MockWebContext context = MockWebContext.create().addRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER, deflateAndBase64(LOGOUT_MESSAGE))
                .addRequestParameter(CasConfiguration.RELAY_STATE_PARAMETER, VALUE).setRequestMethod(HttpConstants.HTTP_METHOD.GET);
        final HttpAction action = (HttpAction) TestsHelper.expectException(() -> casClient.getCredentials(context));
        assertEquals(TEMP_REDIRECT, action.getCode());
        assertEquals("Force redirect to CAS server for front channel logout", action.getMessage());
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
