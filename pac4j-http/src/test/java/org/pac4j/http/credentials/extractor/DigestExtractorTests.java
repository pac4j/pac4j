package org.pac4j.http.credentials.extractor;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.http.credentials.DigestCredentials;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This class tests the {@link DigestAuthExtractor}
 * @author Mircea Carasel
 * @since 1.9.0
 */
public class DigestExtractorTests implements TestsConstants {

    private final static CredentialsExtractor digestExtractor = new DigestAuthExtractor();

    @Test
    public void testRetrieveDigestHeaderComponents() {
        val context = MockWebContext.create();
        context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, DIGEST_AUTHORIZATION_HEADER_VALUE);
        val credentials = (DigestCredentials) digestExtractor.extract(new CallContext(context, new MockSessionStore())).get();
        assertEquals(DIGEST_RESPONSE, credentials.getToken());
        assertEquals(USERNAME, credentials.getUsername());
    }

    @Test
    public void testNotDigest() {
        val context = MockWebContext.create();
        val credentials = digestExtractor.extract(new CallContext(context, new MockSessionStore()));
        assertFalse(credentials.isPresent());
    }

}
