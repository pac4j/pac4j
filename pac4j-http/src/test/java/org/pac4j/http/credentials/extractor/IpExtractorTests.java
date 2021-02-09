package org.pac4j.http.credentials.extractor;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;

import java.util.Optional;

import static org.junit.Assert.*;

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
        final var context = MockWebContext.create().setRemoteAddress(GOOD_IP);
        final var credentials = (TokenCredentials) extractor.extract(context, new MockSessionStore()).get();
        assertEquals(GOOD_IP, credentials.getToken());
    }

    @Test
    public void testRetrieveIpFromHeaderWithProxyIpCheck() {
        final var context = MockWebContext.create().addRequestHeader(HEADER_NAME, GOOD_IP).setRemoteAddress(LOCALHOST);
        final var ipExtractor = new IpExtractor();
        ipExtractor.setProxyIp(LOCALHOST);
        // test for varargs
        ipExtractor.setAlternateIpHeaders("fooBar", HEADER_NAME, "barFoo");
        final var credentials = (TokenCredentials) ipExtractor.extract(context, new MockSessionStore()).get();
        assertEquals(GOOD_IP, credentials.getToken());
        // test for edge case of 1 header
        ipExtractor.setAlternateIpHeaders(HEADER_NAME);
        final var credentials2 = (TokenCredentials) ipExtractor.extract(context, new MockSessionStore()).get();
        assertEquals(GOOD_IP, credentials2.getToken());
    }

    @Test
    public void testRetrieveIpFromHeaderUsingConstructor() {
        final var context = MockWebContext.create().addRequestHeader(HEADER_NAME, GOOD_IP).setRemoteAddress(LOCALHOST);
        // test for varargs
        final var ipExtractor = new IpExtractor("fooBar", HEADER_NAME, "barFoo");
        final var credentials = (TokenCredentials) ipExtractor.extract(context, new MockSessionStore()).get();
        assertEquals(GOOD_IP, credentials.getToken());
        // test for edge case of 1 header
        final var ipExtractor2 = new IpExtractor(HEADER_NAME);
        final var credentials2 = (TokenCredentials) ipExtractor2.extract(context, new MockSessionStore()).get();
        assertEquals(GOOD_IP, credentials2.getToken());
    }

    @Test(expected = TechnicalException.class)
    public void testSetNullIpHeaderChain() {
        final var ipExtractor = new IpExtractor();
        ipExtractor.setAlternateIpHeaders((String[]) null);
    }

    @Test
    public void testNoIp() {
        final var context = MockWebContext.create();
        final var credentials = extractor.extract(context, new MockSessionStore());
        assertFalse(credentials.isPresent());
    }
}
