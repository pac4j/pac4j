package org.pac4j.http.credentials.extractor;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.TokenCredentials;

import static org.junit.Assert.*;

/**
 * This class tests the {@link ParameterExtractor}.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class ParameterExtractorTests implements TestsConstants {

    private final static String GOOD_PARAMETER = "goodParameter";

    private final static ParameterExtractor getExtractor = new ParameterExtractor(GOOD_PARAMETER, true, false, CLIENT_NAME);
    private final static ParameterExtractor postExtractor = new ParameterExtractor(GOOD_PARAMETER, false, true, CLIENT_NAME);

    @Test
    public void testRetrieveGetParameterOk() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod("GET").addRequestParameter(GOOD_PARAMETER, VALUE);
        final TokenCredentials credentials = getExtractor.extract(context);
        assertEquals(VALUE, credentials.getToken());
    }

    @Test
    public void testRetrievePostParameterOk() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod("POST").addRequestParameter(GOOD_PARAMETER, VALUE);
        final TokenCredentials credentials = postExtractor.extract(context);
        assertEquals(VALUE, credentials.getToken());
    }

    @Test
    public void testRetrievePostParameterNotSupported() throws RequiresHttpAction {
        try {
            final MockWebContext context = MockWebContext.create().setRequestMethod("POST").addRequestParameter(GOOD_PARAMETER, VALUE);
            final TokenCredentials credentials = getExtractor.extract(context);
            fail("Should fail");
        } catch (final CredentialsException e) {
            assertEquals("POST requests not supported", e.getMessage());
        }
    }

    @Test
    public void testRetrieveGetParameterNotSupported() throws RequiresHttpAction {
        try {
            final MockWebContext context = MockWebContext.create().setRequestMethod("GET").addRequestParameter(GOOD_PARAMETER, VALUE);
            final TokenCredentials credentials = postExtractor.extract(context);
            fail("Should fail");
        } catch (final CredentialsException e) {
            assertEquals("GET requests not supported", e.getMessage());
        }
    }

    @Test
    public void testRetrieveNoGetParameter() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod("GET");
        final TokenCredentials credentials = getExtractor.extract(context);
        assertNull(credentials);
    }

    @Test
    public void testRetrieveNoPostParameter() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod("POST");
        final TokenCredentials credentials = postExtractor.extract(context);
        assertNull(credentials);
    }
}
