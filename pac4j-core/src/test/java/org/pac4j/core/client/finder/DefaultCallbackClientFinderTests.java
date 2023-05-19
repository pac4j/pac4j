package org.pac4j.core.client.finder;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.client.*;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.http.callback.PathParameterCallbackUrlResolver;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link DefaultCallbackClientFinder}.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public final class DefaultCallbackClientFinderTests implements TestsConstants {

    @Test
    public void testQueryParameter() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        final DirectClient basicAuth = new MockDirectClient("BasicAuth");
        final IndirectClient cas = new MockIndirectClient("cas");
        val clients = new Clients(CALLBACK_URL, facebook, basicAuth, cas);
        val context = MockWebContext.create()
            .addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, "facebook   ");
        final ClientFinder finder = new DefaultCallbackClientFinder();
        val result = finder.find(clients, context, null);
        assertEquals(1, result.size());
        assertEquals(facebook, result.get(0));
    }

    @Test
    public void testPathParameter() {
        final IndirectClient azure = new MockIndirectClient("azure");
        azure.setCallbackUrlResolver(new PathParameterCallbackUrlResolver());
        val clients = new Clients(CALLBACK_URL, azure);
        val context = MockWebContext.create().setPath("/   AZURE   ");
        final ClientFinder finder = new DefaultCallbackClientFinder();
        val result = finder.find(clients, context, null);
        assertEquals(1, result.size());
        assertEquals(azure, result.get(0));
    }

    @Test
    public void testDefaultClientDirectClientInURL() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        final DirectClient basicAuth = new MockDirectClient("BasicAuth");
        val clients = new Clients(CALLBACK_URL, basicAuth, facebook);
        val context = MockWebContext.create()
            .addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, "basicauth");
        ClientFinder finder = new DefaultCallbackClientFinder();
        val result = finder.find(clients, context, "Facebook");
        assertEquals(1, result.size());
        assertEquals(facebook, result.get(0));
    }

    @Test
    public void testDefaultClientIndirectClientInURL() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        final IndirectClient twitter = new MockIndirectClient("Twitter");
        val clients = new Clients(CALLBACK_URL, twitter, facebook);
        val context = MockWebContext.create()
            .addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, "Twitter");
        ClientFinder finder = new DefaultCallbackClientFinder();
        val result = finder.find(clients, context, "Twitter");
        assertEquals(1, result.size());
        assertEquals(twitter, result.get(0));
    }

    @Test
    public void testDefaultClientNoIndirectClientInURL() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        final IndirectClient twitter = new MockIndirectClient("Twitter");
        val clients = new Clients(CALLBACK_URL, twitter, facebook);
        ClientFinder finder = new DefaultCallbackClientFinder();
        val result = finder.find(clients, MockWebContext.create(), "Facebook");
        assertEquals(1, result.size());
        assertEquals(facebook, result.get(0));
    }

    @Test
    public void testOneIndirectClientNoIndirectClientInURL() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        val clients = new Clients(CALLBACK_URL, facebook);
        ClientFinder finder = new DefaultCallbackClientFinder();
        val result = finder.find(clients, MockWebContext.create(), null);
        assertEquals(1, result.size());
        assertEquals(facebook, result.get(0));
    }
}
