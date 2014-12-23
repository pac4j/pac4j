/*
  Copyright 2012 - 2014 Jerome Leleu

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

import junit.framework.TestCase;

import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
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
    
    public void testMissingCasUrls() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(casClient, "casLoginUrl and casPrefixUrl cannot be both blank");
    }
    
    public void testMissingSlashOnPrefixUrl() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        casClient.setCasPrefixUrl(PREFIX_URL_WITHOUT_SLASH);
        casClient.init();
        assertEquals(PREFIX_URL, casClient.getCasPrefixUrl());
    }
    
    public void testInitPrefixUrl() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        assertEquals(null, casClient.getCasPrefixUrl());
        casClient.init();
        assertEquals(PREFIX_URL, casClient.getCasPrefixUrl());
    }
    
    public void testInitLoginUrl() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasPrefixUrl(PREFIX_URL);
        assertEquals(null, casClient.getCasLoginUrl());
        casClient.init();
        assertEquals(LOGIN_URL, casClient.getCasLoginUrl());
    }
    
    public void testRenew() throws RequiresHttpAction {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        MockWebContext context = MockWebContext.create();
		casClient.redirect(context, false, false);
        assertFalse(context.getResponseLocation().indexOf("renew=true") >= 0);
        casClient.setRenew(true);
        casClient.reinit();
        context = MockWebContext.create();
        casClient.redirect(context, false, false);
        assertTrue(context.getResponseLocation().indexOf("renew=true") >= 0);
    }
    
    public void testGateway() throws RequiresHttpAction {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        final MockWebContext context = MockWebContext.create();
        casClient.redirect(context, false, false);
        assertFalse(context.getResponseLocation().indexOf("gateway=true") >= 0);
        casClient.setGateway(true);
        casClient.reinit();
        casClient.redirect(context, false, false);
        assertTrue(context.getResponseLocation().indexOf("gateway=true") >= 0);
        final CasCredentials credentials = casClient.getCredentials(context);
        assertNull(credentials);
    }
    
    public void testNullLogoutHandler() {
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        casClient.setLogoutHandler(null);
        TestsHelper.initShouldFail(casClient, "logoutHandler cannot be null");
    }
    
    public void testLogout() {
        final String logoutRequest = "<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" ID=\"LR-1-B2b0CVRW5eSvPBZPsAVXdNPj7jee4SWjr9y\" Version=\"2.0\" IssueInstant=\"2012-12-19T15:30:55Z\"><saml:NameID xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">@NOT_USED@</saml:NameID><samlp:SessionIndex>ST-1-FUUhL26EgrkcD6I2Mry9-cas01.example.org</samlp:SessionIndex></samlp:LogoutRequest>";
        final CasClient casClient = new CasClient();
        casClient.setCallbackUrl(CALLBACK_URL);
        casClient.setCasLoginUrl(LOGIN_URL);
        casClient.init();
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
}
