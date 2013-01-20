/*
  Copyright 2012 - 2013 Jerome Leleu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.cas.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.pac4j.cas.client.CasClient.CasProtocol;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.ClientException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

/**
 * This class tests the {@link CasClient} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class TestCasClient extends TestCase implements TestsConstants {
    
    private static final String PREFIX_URL = "http://myserver/";
    
    private static final String PREFIX_URL_WITHOUT_SLASH = "http://myserver";
    
    private static final CasProtocol PROTOCOL = CasProtocol.CAS20;
    
    public void testMissingCasUrls() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(casClient, "casLoginUrl and casPrefixUrl cannot be both blank");
    }
    
    public void testMissingSlashOnPrefixUrl() throws ClientException {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        casClient.setCasPrefixUrl(PREFIX_URL_WITHOUT_SLASH);
        casClient.init();
        assertEquals(PREFIX_URL, casClient.getCasPrefixUrl());
    }
    
    public void testInitPrefixUrl() throws ClientException {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        assertEquals(null, casClient.getCasPrefixUrl());
        casClient.init();
        assertEquals(PREFIX_URL, casClient.getCasPrefixUrl());
    }
    
    public void testInitLoginUrl() throws ClientException {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasPrefixUrl(PREFIX_URL);
        assertEquals(null, casClient.getCasLoginUrl());
        casClient.init();
        assertEquals(LOGIN_URL, casClient.getCasLoginUrl());
    }
    
    public void testCloneCasClient() throws ClientException {
        final CasClient oldClient = new CasClient();
        oldClient.setCasLoginUrl(LOGIN_URL);
        oldClient.setCasPrefixUrl(PREFIX_URL);
        oldClient.setCasProtocol(PROTOCOL);
        final CasClient newClient = (CasClient) oldClient.clone();
        assertEquals(oldClient.getCallbackUrl(), newClient.getCallbackUrl());
        assertEquals(oldClient.getCasLoginUrl(), newClient.getCasLoginUrl());
        assertEquals(oldClient.getCasPrefixUrl(), newClient.getCasPrefixUrl());
        assertEquals(oldClient.getCasProtocol(), newClient.getCasProtocol());
    }
    
    public void testRenew() throws ClientException {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        assertFalse(casClient.getRedirectionUrl(MockWebContext.create()).indexOf("renew=true") >= 0);
        casClient.setRenew(true);
        casClient.reinit();
        assertTrue(casClient.getRedirectionUrl(MockWebContext.create()).indexOf("renew=true") >= 0);
    }
    
    public void testGateway() throws ClientException {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        assertFalse(casClient.getRedirectionUrl(MockWebContext.create()).indexOf("gateway=true") >= 0);
        casClient.setGateway(true);
        casClient.reinit();
        assertTrue(casClient.getRedirectionUrl(MockWebContext.create()).indexOf("gateway=true") >= 0);
    }
    
    public void testNullLogoutHandler() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        casClient.setLogoutHandler(null);
        TestsHelper.initShouldFail(casClient, "logoutHandler cannot be null");
    }
    
    public void testLogout() throws ClientException {
        final String logoutRequest = "<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" ID=\"LR-1-B2b0CVRW5eSvPBZPsAVXdNPj7jee4SWjr9y\" Version=\"2.0\" IssueInstant=\"2012-12-19T15:30:55Z\"><saml:NameID xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">@NOT_USED@</saml:NameID><samlp:SessionIndex>ST-1-FUUhL26EgrkcD6I2Mry9-cas01.example.org</samlp:SessionIndex></samlp:LogoutRequest>";
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        casClient.init();
        final WebContext context = mock(WebContext.class);
        when(context.getRequestParameter("logoutRequest")).thenReturn(logoutRequest);
        when(context.getRequestMethod()).thenReturn("POST");
        assertNull(casClient.getCredentials(context));
        verify(context).invalidateSession();
    }
}
