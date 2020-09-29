package org.pac4j.core.credentials.extractor;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.util.TestsHelper;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * This class tests the {@link HeaderExtractor}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class HeaderExtractorTests implements TestsConstants {

    private final static String GOOD_HEADER = "goodHeader";
    private final static String BAD_HEADER = "badHeader";

    private final static String GOOD_PREFIX = "goodPrefix ";
    private final static String BAD_PREFIX = "badPrefix ";

    private final static HeaderExtractor extractor = new HeaderExtractor(GOOD_HEADER, GOOD_PREFIX);

    @Test
    public void testRetrieveHeaderOk() {
        final MockWebContext context = MockWebContext.create().addRequestHeader(GOOD_HEADER, GOOD_PREFIX + VALUE);
        final TokenCredentials credentials = (TokenCredentials) extractor.extract(context).get();
        assertEquals(VALUE, credentials.getToken());
    }

    @Test
    public void testBadHeader() {
        final MockWebContext context = MockWebContext.create().addRequestHeader(BAD_HEADER, GOOD_PREFIX + VALUE);
        final Optional<Credentials> credentials = extractor.extract(context);
        assertFalse(credentials.isPresent());
    }

    @Test
    public void testBadPrefix() {
        final MockWebContext context = MockWebContext.create().addRequestHeader(GOOD_HEADER, BAD_PREFIX + VALUE);
        TestsHelper.expectException(() -> extractor.extract(context), CredentialsException.class,
            "Wrong prefix for header: " + GOOD_HEADER);
    }
}
