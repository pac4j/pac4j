package org.pac4j.http.credentials.extractor;

import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.util.TestsHelper;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * This class tests the {@link ParameterExtractor}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class ParameterExtractorTests implements TestsConstants {

    private final static String GOOD_PARAMETER = "goodParameter";

    private final static ParameterExtractor getExtractor = new ParameterExtractor(GOOD_PARAMETER, true, false);
    private final static ParameterExtractor postExtractor = new ParameterExtractor(GOOD_PARAMETER, false, true);

    @Test
    public void testRetrieveGetParameterOk() {
        final var context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.GET.name())
            .addRequestParameter(GOOD_PARAMETER, VALUE);
        final var credentials = (TokenCredentials) getExtractor.extract(context, new MockSessionStore()).get();
        assertEquals(VALUE, credentials.getToken());
    }

    @Test
    public void testRetrievePostParameterOk() {
        final var context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(GOOD_PARAMETER, VALUE);
        final var credentials = (TokenCredentials) postExtractor.extract(context, new MockSessionStore()).get();
        assertEquals(VALUE, credentials.getToken());
    }

    @Test
    public void testRetrievePostParameterNotSupported() {
        final var context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(GOOD_PARAMETER, VALUE);
        TestsHelper.expectException(() -> getExtractor.extract(context, new MockSessionStore()),
            CredentialsException.class, "POST requests not supported");
    }

    @Test
    public void testRetrieveGetParameterNotSupported() {
        final var context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.GET.name())
            .addRequestParameter(GOOD_PARAMETER, VALUE);
        TestsHelper.expectException(() -> postExtractor.extract(context, new MockSessionStore()),
            CredentialsException.class, "GET requests not supported");
    }

    @Test
    public void testRetrieveNoGetParameter() {
        final var context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.GET.name());
        final var credentials = getExtractor.extract(context, new MockSessionStore());
        assertFalse(credentials.isPresent());
    }

    @Test
    public void testRetrieveNoPostParameter() {
        final var context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name());
        final var credentials = postExtractor.extract(context, new MockSessionStore());
        assertFalse(credentials.isPresent());
    }
}
