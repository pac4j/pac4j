package org.pac4j.cas.client;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.http.HttpAction;
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
        TestsHelper.initShouldFail(client,
                "callbackUrl cannot be blank: set it up either on this IndirectClient or on the global Config");
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
        final HttpAction action = (HttpAction) TestsHelper.expectException(
            () -> client.getCredentials(context.addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET, VALUE)));
        assertEquals(200, action.getCode());
    }

    @Test
    public void testMissingPgtiou() {
        final CasProxyReceptor client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create();
        final HttpAction action = (HttpAction) TestsHelper.expectException(
            () -> client.getCredentials(context.addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET_IOU, VALUE)));
        assertEquals(200, action.getCode());
    }

    @Test
    public void testOk() {
        final CasProxyReceptor client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        final MockWebContext context = MockWebContext.create()
            .addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET, VALUE)
            .addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET_IOU, VALUE);
        final HttpAction action = (HttpAction) TestsHelper.expectException(() -> client.getCredentials(context));
        assertEquals(200, action.getCode());
    }
}
