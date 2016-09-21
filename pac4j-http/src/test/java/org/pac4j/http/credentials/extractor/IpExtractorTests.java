package org.pac4j.http.credentials.extractor;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.TokenCredentials;

import static org.junit.Assert.*;

/**
 * This class tests the {@link IpExtractor}.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class IpExtractorTests implements TestsConstants {

    private final static String GOOD_IP = "goodIp";

    private final static IpExtractor extractor = new IpExtractor(CLIENT_NAME);

    @Test
    public void testRetrieveIpOk() throws HttpAction {
        final MockWebContext context = MockWebContext.create().setRemoteAddress(GOOD_IP);
        final TokenCredentials credentials = extractor.extract(context);
        assertEquals(GOOD_IP, credentials.getToken());
    }

    @Test
    public void testRetrieveIpFromHeader() throws HttpAction {
        final MockWebContext context = MockWebContext.create().addRequestHeader(HEADER_NAME, GOOD_IP);
        final IpExtractor ipExtractor = new IpExtractor(CLIENT_NAME);
        ipExtractor.setAlternateIpHeader(HEADER_NAME);
        final TokenCredentials credentials = ipExtractor.extract(context);
        assertEquals(GOOD_IP, credentials.getToken());
    }

    @Test
    public void testNoIp() throws HttpAction {
        final MockWebContext context = MockWebContext.create();
        final TokenCredentials credentials = extractor.extract(context);
        assertNull(credentials);
    }
}
