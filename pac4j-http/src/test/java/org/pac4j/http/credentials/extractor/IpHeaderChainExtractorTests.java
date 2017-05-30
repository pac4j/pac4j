package org.pac4j.http.credentials.extractor;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This class tests the {@link IpHeaderChainExtractor}.
 *
 * @author Guilherme I F L Weizenmann
 * @since 2.1.0
 */
public final class IpHeaderChainExtractorTests implements TestsConstants {

    private final static String GOOD_IP = "goodIp";

    private final static IpHeaderChainExtractor extractor = new IpHeaderChainExtractor(CLIENT_NAME);

    @Test
    public void testRetrieveIpOk() throws HttpAction {
        final MockWebContext context = MockWebContext.create().setRemoteAddress(GOOD_IP);
        final TokenCredentials credentials = extractor.extract(context);
        assertEquals(GOOD_IP, credentials.getToken());
    }

    @Test
    public void testRetrieveIpFromHeader() throws HttpAction {
        final MockWebContext context = MockWebContext.create().addRequestHeader(HEADER_NAME, GOOD_IP);
        final IpHeaderChainExtractor ipExtractor = new IpHeaderChainExtractor(CLIENT_NAME);
        ipExtractor.setAlternateIpHeader("fooBar", HEADER_NAME, "barFoo");
        final TokenCredentials credentials = ipExtractor.extract(context);
        assertEquals(GOOD_IP, credentials.getToken());
    }

    @Test(expected = NullPointerException.class)
    public void testSetNullIpHeaderChain() throws HttpAction {
        final IpHeaderChainExtractor ipExtractor = new IpHeaderChainExtractor(CLIENT_NAME);
        ipExtractor.setAlternateIpHeader(null);
    }

    @Test
    public void testNoIp() throws HttpAction {
        final MockWebContext context = MockWebContext.create();
        final TokenCredentials credentials = extractor.extract(context);
        assertNull(credentials);
    }
}
