package org.pac4j.cas.client;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the {@link CasProxyReceptor} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CasProxyReceptorTests implements TestsConstants {

    @Test
    public void testMissingCallbackUrl() {
        val client = new CasProxyReceptor();
        TestsHelper.initShouldFail(client,
                "callbackUrl cannot be blank: set it up either on this IndirectClient or on the global Config");
    }

    @Test
    public void testMissingStorage() {
        val client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        client.setStore(null);
        TestsHelper.initShouldFail(client, "store cannot be null");
    }

    @Test
    public void testMissingPgt() {
        val client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        val context = MockWebContext.create();
        val action = (HttpAction) TestsHelper.expectException(
            () -> client.getCredentials(context.addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET, VALUE),
                new MockSessionStore()));
        assertEquals(200, action.getCode());
    }

    @Test
    public void testMissingPgtiou() {
        val client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        val context = MockWebContext.create();
        val action = (HttpAction) TestsHelper.expectException(
            () -> client.getCredentials(context.addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET_IOU, VALUE),
                new MockSessionStore()));
        assertEquals(200, action.getCode());
    }

    @Test
    public void testOk() {
        val client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        val context = MockWebContext.create()
            .addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET, VALUE)
            .addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET_IOU, VALUE);
        val action = (HttpAction) TestsHelper.expectException(() -> client.getCredentials(context, new MockSessionStore()));
        assertEquals(200, action.getCode());
    }
}
