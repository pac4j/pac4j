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

import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

/**
 * This class tests the {@link CasProxyReceptor} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class TestCasProxyReceptor extends TestCase implements TestsConstants {
    
    public void testClone() {
        final CasProxyReceptor oldClient = new CasProxyReceptor();
        oldClient.setCallbackUrl(CALLBACK_URL);
        oldClient.setMillisBetweenCleanUps(MILLIS_BETWEEN_CLEANUPS);
        final ProxyGrantingTicketStorageImpl proxyGrantingTicketStorageImpl = new ProxyGrantingTicketStorageImpl();
        oldClient.setProxyGrantingTicketStorage(proxyGrantingTicketStorageImpl);
        oldClient.setName(TYPE);
        final CasProxyReceptor client = (CasProxyReceptor) oldClient.clone();
        assertEquals(oldClient.getCallbackUrl(), client.getCallbackUrl());
        assertEquals(oldClient.getMillisBetweenCleanUps(), client.getMillisBetweenCleanUps());
        assertEquals(oldClient.getProxyGrantingTicketStorage(), client.getProxyGrantingTicketStorage());
        assertEquals(oldClient.getName(), client.getName());
    }
    
    public void testMissingCallbackUrl() {
        final CasProxyReceptor client = new CasProxyReceptor();
        TestsHelper.initShouldFail(client, "callbackUrl cannot be blank");
    }
    
    public void testMissingStorage() {
        final CasProxyReceptor client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        client.setProxyGrantingTicketStorage(null);
        TestsHelper.initShouldFail(client, "proxyGrantingTicketStorage cannot be null");
    }
    
    public void testMissingPgt() {
        final CasProxyReceptor client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        try {
            client.getCredentials(context.addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET, VALUE));
        } catch (final RequiresHttpAction e) {
            assertEquals(200, context.getResponseStatus());
            assertEquals("", context.getResponseContent());
            assertEquals("Missing proxyGrantingTicket or proxyGrantingTicketIou", e.getMessage());
        }
    }
    
    public void testMissingPgtiou() {
        final CasProxyReceptor client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        try {
            client.getCredentials(context.addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET_IOU, VALUE));
        } catch (final RequiresHttpAction e) {
            assertEquals(200, context.getResponseStatus());
            assertEquals("", context.getResponseContent());
            assertEquals("Missing proxyGrantingTicket or proxyGrantingTicketIou", e.getMessage());
        }
    }
    
    public void testOk() {
        final CasProxyReceptor client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create()
            .addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET, VALUE)
            .addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET_IOU, VALUE);
        try {
            client.getCredentials(context);
            fail("should throw RequiresHttpAction");
        } catch (final RequiresHttpAction e) {
            assertEquals(200, context.getResponseStatus());
            assertTrue(context.getResponseContent().length() > 0);
            assertEquals("No credential for CAS proxy receptor -> returns ok", e.getMessage());
        }
    }
}
