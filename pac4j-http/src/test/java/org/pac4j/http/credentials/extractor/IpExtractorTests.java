package org.pac4j.http.credentials.extractor;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
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

    private final static String GOOD_IP = "1.2.3.4";

    private final static IpExtractor extractor = new IpExtractor(CLIENT_NAME);

    @Test
    public void testRetrieveIpOk() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create().setRemoteAddress(GOOD_IP);
        final TokenCredentials credentials = extractor.extract(context);
        assertEquals(GOOD_IP, credentials.getToken());
    }

    @Test
    public void testNoIp() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create();
        final TokenCredentials credentials = extractor.extract(context);
        assertNull(credentials);
    }
}
