package org.pac4j.core.client.finder;

import org.junit.Test;
import org.pac4j.core.client.*;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.http.callback.PathParameterCallbackUrlResolver;
import org.pac4j.core.util.TestsConstants;

import java.util.List;

import static org.junit.Assert.*;

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
        final Clients clients = new Clients(CALLBACK_URL, facebook, basicAuth, cas);
        clients.init();
        final MockWebContext context = MockWebContext.create()
            .addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, "facebook   ");
        final ClientFinder finder = new DefaultCallbackClientFinder();
        final List<Client<? extends Credentials>> result = finder.find(clients, context, null);
        assertEquals(1, result.size());
        assertEquals(facebook, result.get(0));
    }

    @Test
    public void testPathParameter() {
        final IndirectClient azure = new MockIndirectClient("azure");
        azure.setCallbackUrlResolver(new PathParameterCallbackUrlResolver());
        final Clients clients = new Clients(CALLBACK_URL, azure);
        clients.init();
        final MockWebContext context = MockWebContext.create().setPath("/   AZURE   ");
        final ClientFinder finder = new DefaultCallbackClientFinder();
        final List<Client<? extends Credentials>> result = finder.find(clients, context, null);
        assertEquals(1, result.size());
        assertEquals(azure, result.get(0));
    }

    @Test
    public void testDefaultClientDirectClientInURL() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        final DirectClient basicAuth = new MockDirectClient("BasicAuth");
        final Clients clients = new Clients(CALLBACK_URL, basicAuth, facebook);
        clients.init();
        final MockWebContext context = MockWebContext.create()
            .addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, "basicauth");
        final DefaultCallbackClientFinder finder = new DefaultCallbackClientFinder();
        final List<Client<? extends Credentials>> result = finder.find(clients, context, "Facebook");
        assertEquals(1, result.size());
        assertEquals(facebook, result.get(0));
    }

    @Test
    public void testDefaultClientIndirectClientInURL() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        final IndirectClient twitter = new MockIndirectClient("Twitter");
        final Clients clients = new Clients(CALLBACK_URL, twitter, facebook);
        clients.init();
        final MockWebContext context = MockWebContext.create()
            .addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, "Twitter");
        final DefaultCallbackClientFinder finder = new DefaultCallbackClientFinder();
        final List<Client<? extends Credentials>> result = finder.find(clients, context, "Twitter");
        assertEquals(1, result.size());
        assertEquals(twitter, result.get(0));
    }

    @Test
    public void testDefaultClientNoIndirectClientInURL() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        final IndirectClient twitter = new MockIndirectClient("Twitter");
        final Clients clients = new Clients(CALLBACK_URL, twitter, facebook);
        clients.init();
        final DefaultCallbackClientFinder finder = new DefaultCallbackClientFinder();
        final List<Client<? extends Credentials>> result = finder.find(clients, MockWebContext.create(), "Facebook");
        assertEquals(1, result.size());
        assertEquals(facebook, result.get(0));
    }

    @Test
    public void testOneIndirectClientNoIndirectClientInURL() {
        final IndirectClient facebook = new MockIndirectClient("Facebook");
        final Clients clients = new Clients(CALLBACK_URL, facebook);
        clients.init();
        final DefaultCallbackClientFinder finder = new DefaultCallbackClientFinder();
        final List<Client<? extends Credentials>> result = finder.find(clients, MockWebContext.create(), null);
        assertEquals(1, result.size());
        assertEquals(facebook, result.get(0));
    }
}
