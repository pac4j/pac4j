package org.pac4j.cas.client;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
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
        final var client = new CasProxyReceptor();
        TestsHelper.initShouldFail(client,
                "callbackUrl cannot be blank: set it up either on this IndirectClient or on the global Config");
    }

    @Test
    public void testMissingStorage() {
        final var client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        client.setStore(null);
        TestsHelper.initShouldFail(client, "store cannot be null");
    }

    @Test
    public void testMissingPgt() {
        final var client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        final var context = MockWebContext.create();
        final var action = (HttpAction) TestsHelper.expectException(
            () -> client.getCredentials(context.addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET, VALUE),
                new MockSessionStore()));
        assertEquals(200, action.getCode());
    }

    @Test
    public void testMissingPgtiou() {
        final var client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        final var context = MockWebContext.create();
        final var action = (HttpAction) TestsHelper.expectException(
            () -> client.getCredentials(context.addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET_IOU, VALUE),
                new MockSessionStore()));
        assertEquals(200, action.getCode());
    }

    @Test
    public void testOk() {
        final var client = new CasProxyReceptor();
        client.setCallbackUrl(CALLBACK_URL);
        final var context = MockWebContext.create()
            .addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET, VALUE)
            .addRequestParameter(CasProxyReceptor.PARAM_PROXY_GRANTING_TICKET_IOU, VALUE);
        final var action = (HttpAction) TestsHelper.expectException(() -> client.getCredentials(context, new MockSessionStore()));
        assertEquals(200, action.getCode());
    }
}
