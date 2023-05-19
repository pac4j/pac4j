package org.pac4j.http.credentials.extractor;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

    private static final CredentialsExtractor extractor = new IpExtractor();

    @Test
    public void testRetrieveIpOk() {
        val context = MockWebContext.create().setRemoteAddress(GOOD_IP);
        val credentials = (TokenCredentials) extractor.extract(new CallContext(context, new MockSessionStore())).get();
        assertEquals(GOOD_IP, credentials.getToken());
    }

    @Test
    public void testRetrieveIpFromHeaderWithProxyIpCheck() {
        val context = MockWebContext.create().addRequestHeader(HEADER_NAME, GOOD_IP).setRemoteAddress(LOCALHOST);
        val ipExtractor = new IpExtractor();
        ipExtractor.setProxyIp(LOCALHOST);
        // test for varargs
        ipExtractor.setAlternateIpHeaders("fooBar", HEADER_NAME, "barFoo");
        val credentials = (TokenCredentials) ipExtractor.extract(new CallContext(context, new MockSessionStore())).get();
        assertEquals(GOOD_IP, credentials.getToken());
        // test for edge case of 1 header
        ipExtractor.setAlternateIpHeaders(HEADER_NAME);
        val credentials2 = (TokenCredentials) ipExtractor.extract(new CallContext(context, new MockSessionStore())).get();
        assertEquals(GOOD_IP, credentials2.getToken());
    }

    @Test
    public void testRetrieveIpFromHeaderUsingConstructor() {
        val context = MockWebContext.create().addRequestHeader(HEADER_NAME, GOOD_IP).setRemoteAddress(LOCALHOST);
        // test for varargs
        CredentialsExtractor ipExtractor = new IpExtractor("fooBar", HEADER_NAME, "barFoo");
        val credentials = (TokenCredentials) ipExtractor.extract(new CallContext(context, new MockSessionStore())).get();
        assertEquals(GOOD_IP, credentials.getToken());
        // test for edge case of 1 header
        CredentialsExtractor ipExtractor2 = new IpExtractor(HEADER_NAME);
        val credentials2 = (TokenCredentials) ipExtractor2.extract(new CallContext(context, new MockSessionStore())).get();
        assertEquals(GOOD_IP, credentials2.getToken());
    }

    @Test(expected = TechnicalException.class)
    public void testSetNullIpHeaderChain() {
        val ipExtractor = new IpExtractor();
        ipExtractor.setAlternateIpHeaders((String[]) null);
    }

    @Test
    public void testNoIp() {
        val context = MockWebContext.create();
        val credentials = extractor.extract(new CallContext(context, new MockSessionStore()));
        assertFalse(credentials.isPresent());
    }
}
