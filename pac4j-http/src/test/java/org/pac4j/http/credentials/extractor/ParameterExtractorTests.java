package org.pac4j.http.credentials.extractor;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.util.TestsHelper;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.pac4j.core.context.HttpConstants.*;

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
        final MockWebContext context = MockWebContext.create().setRequestMethod(HTTP_METHOD.GET.name())
            .addRequestParameter(GOOD_PARAMETER, VALUE);
        final Optional<TokenCredentials> credentials = getExtractor.extract(context);
        assertEquals(VALUE, credentials.get().getToken());
    }

    @Test
    public void testRetrievePostParameterOk() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HTTP_METHOD.POST.name())
            .addRequestParameter(GOOD_PARAMETER, VALUE);
        final Optional<TokenCredentials> credentials = postExtractor.extract(context);
        assertEquals(VALUE, credentials.get().getToken());
    }

    @Test
    public void testRetrievePostParameterNotSupported() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HTTP_METHOD.POST.name())
            .addRequestParameter(GOOD_PARAMETER, VALUE);
        TestsHelper.expectException(() -> getExtractor.extract(context), CredentialsException.class, "POST requests not supported");
    }

    @Test
    public void testRetrieveGetParameterNotSupported() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HTTP_METHOD.GET.name())
            .addRequestParameter(GOOD_PARAMETER, VALUE);
        TestsHelper.expectException(() -> postExtractor.extract(context), CredentialsException.class, "GET requests not supported");
    }

    @Test
    public void testRetrieveNoGetParameter() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HTTP_METHOD.GET.name());
        final Optional<TokenCredentials> credentials = getExtractor.extract(context);
        assertFalse(credentials.isPresent());
    }

    @Test
    public void testRetrieveNoPostParameter() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HTTP_METHOD.POST.name());
        final Optional<TokenCredentials> credentials = postExtractor.extract(context);
        assertFalse(credentials.isPresent());
    }
}
