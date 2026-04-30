package org.pac4j.http.credentials.extractor;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.http.credentials.DigestCredentials;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;
import org.pac4j.test.util.TestsConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class tests the {@link DigestAuthExtractor}
 * @author Mircea Carasel
 * @since 1.9.0
 */
public class DigestExtractorTests implements TestsConstants {

    private final static CredentialsExtractor digestExtractor = new DigestAuthExtractor();
    private static final String VALID_DIGEST_AUTHORIZATION_HEADER_VALUE = "Digest username=\"" + USERNAME + "\",realm=\""
        + REALM + "\",nonce=\"" + NONCE + "\",uri=\"" + URI + "\",response=\"" + DIGEST_RESPONSE + "\",qop=\""
        + QOP + "\",nc=\"" + NC + "\",cnonce=\"" + CNONCE + "\"";

    @Test
    public void testRetrieveDigestHeaderComponents() {
        val context = MockWebContext.create();
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, VALID_DIGEST_AUTHORIZATION_HEADER_VALUE);
        val credentials = (DigestCredentials) digestExtractor.extract(new CallContext(context, new MockSessionStore())).get();
        assertEquals(DIGEST_RESPONSE, credentials.getToken());
        assertEquals(USERNAME, credentials.getUsername());
    }
    @Test
    public void testRetrieveDigestHeaderComponentsWithCommaAndEscapedQuote() {
        val context = MockWebContext.create();
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER,
            "Digest username=\"user,\\\"name\",realm=\"" + REALM + "\",nonce=\"" + NONCE + "\",uri=\"" + URI
                + "\",response=\"" + DIGEST_RESPONSE + "\",qop=\"auth\",nc=\"" + NC + "\",cnonce=\"" + CNONCE + "\"");
        val credentials = (DigestCredentials) digestExtractor.extract(new CallContext(context, new MockSessionStore())).get();
        assertEquals("user,\"name", credentials.getUsername());
    }

    @Test
    public void testBadQop() {
        val context = MockWebContext.create();
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER,
            "Digest username=\"" + USERNAME + "\",realm=\"" + REALM + "\",nonce=\"" + NONCE + "\",uri=\"" + URI
                + "\",response=\"" + DIGEST_RESPONSE + "\",qop=\"auth-int\",nc=\"" + NC + "\",cnonce=\"" + CNONCE + "\"");
        assertThrows(CredentialsException.class, () -> digestExtractor.extract(new CallContext(context, new MockSessionStore())));
    }

    @Test
    public void testMalformedHeader() {
        val context = MockWebContext.create();
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, DIGEST_AUTHORIZATION_HEADER_VALUE);
        assertThrows(CredentialsException.class, () -> digestExtractor.extract(new CallContext(context, new MockSessionStore())));
    }

    @Test
    public void testNotDigest() {
        val context = MockWebContext.create();
        val credentials = digestExtractor.extract(new CallContext(context, new MockSessionStore()));
        assertFalse(credentials.isPresent());
    }

}
