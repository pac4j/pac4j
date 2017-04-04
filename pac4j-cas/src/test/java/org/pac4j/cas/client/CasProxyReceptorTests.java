package org.pac4j.cas.client;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.*;

/**
 * This class tests the {@link CasProxyReceptor} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CasProxyReceptorTests implements TestsConstants {

    @Test
    public void testMissingCallbackUrl() {
        final CasProxyReceptor client = new CasProxyReceptor();
        TestsHelper.initShouldFail(client, "callbackUrl cannot be blank");
    }

    @Test
    public void testMissingStorage() {
        final CasProxyReceptor client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        client.setStore(null);
        TestsHelper.initShouldFail(client, "store cannot be null");
    }

    @Test
    public void testMissingPgt() {
        final CasProxyReceptor client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        try {
            client.getCredentials(context.addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET, VALUE));
        } catch (final HttpAction e) {
            assertEquals(200, context.getResponseStatus());
            assertEquals("", context.getResponseContent());
            assertEquals("Missing proxyGrantingTicket or proxyGrantingTicketIou", e.getMessage());
        }
    }

    @Test
    public void testMissingPgtiou() {
        final CasProxyReceptor client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        TestsHelper.expectException(() -> client.getCredentials(context.addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET_IOU, VALUE)), HttpAction.class,
                "Missing proxyGrantingTicket or proxyGrantingTicketIou");
        assertEquals(200, context.getResponseStatus());
        assertEquals("", context.getResponseContent());
    }

    @Test
    public void testOk() {
        final CasProxyReceptor client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create()
            .addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET, VALUE)
            .addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET_IOU, VALUE);
        TestsHelper.expectException(() -> client.getCredentials(context), HttpAction.class, "No credential for CAS proxy receptor -> returns ok");
        assertEquals(200, context.getResponseStatus());
        assertTrue(context.getResponseContent().length() > 0);
    }
}
