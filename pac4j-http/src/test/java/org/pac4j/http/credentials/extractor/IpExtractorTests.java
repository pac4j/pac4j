package org.pac4j.http.credentials.extractor;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This class tests the {@link IpExtractor}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class IpExtractorTests implements TestsConstants {

    private final static String GOOD_IP = "goodIp";
    @SuppressWarnings("PMD")
    private final static String LOCALHOST = "127.0.0.1";

    private static final IpExtractor extractor = new IpExtractor();

    @Test
    public void testRetrieveIpOk() {
        final MockWebContext context = MockWebContext.create().setRemoteAddress(GOOD_IP);
        final TokenCredentials credentials = extractor.extract(context);
        assertEquals(GOOD_IP, credentials.getToken());
    }

    @Test
    public void testRetrieveIpFromHeaderWithProxyIpCheck() {
        final MockWebContext context = MockWebContext.create().addRequestHeader(HEADER_NAME, GOOD_IP).setRemoteAddress(LOCALHOST);
        final IpExtractor ipExtractor = new IpExtractor();
        ipExtractor.setProxyIp(LOCALHOST);
        // test for varargs
        ipExtractor.setAlternateIpHeaders("fooBar", HEADER_NAME, "barFoo");
        final TokenCredentials credentials = ipExtractor.extract(context);
        assertEquals(GOOD_IP, credentials.getToken());
        // test for edge case of 1 header
        ipExtractor.setAlternateIpHeaders(HEADER_NAME);
        final TokenCredentials credentials2 = ipExtractor.extract(context);
        assertEquals(GOOD_IP, credentials2.getToken());
    }

    @Test
    public void testRetrieveIpFromHeaderUsingConstructor() {
        final MockWebContext context = MockWebContext.create().addRequestHeader(HEADER_NAME, GOOD_IP).setRemoteAddress(LOCALHOST);
        // test for varargs
        final IpExtractor ipExtractor = new IpExtractor("fooBar", HEADER_NAME, "barFoo");
        final TokenCredentials credentials = ipExtractor.extract(context);
        assertEquals(GOOD_IP, credentials.getToken());
        // test for edge case of 1 header
        final IpExtractor ipExtractor2 = new IpExtractor(HEADER_NAME);
        final TokenCredentials credentials2 = ipExtractor2.extract(context);
        assertEquals(GOOD_IP, credentials2.getToken());
    }

    @Test(expected = TechnicalException.class)
    public void testSetNullIpHeaderChain() {
        final IpExtractor ipExtractor = new IpExtractor();
        ipExtractor.setAlternateIpHeaders((String[]) null);
    }

    @Test
    public void testNoIp() {
        final MockWebContext context = MockWebContext.create();
        final TokenCredentials credentials = extractor.extract(context);
        assertNull(credentials);
    }

    @Test
    public void testWrongProxyIp() {
        final MockWebContext context = MockWebContext.create();
        final TokenCredentials credentials = extractor.extract(context);
        assertNull(credentials);
    }
}
