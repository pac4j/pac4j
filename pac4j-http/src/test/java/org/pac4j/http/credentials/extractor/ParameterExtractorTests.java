package org.pac4j.http.credentials.extractor;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        val context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.GET.name())
            .addRequestParameter(GOOD_PARAMETER, VALUE);
        val credentials = (TokenCredentials) getExtractor.extract(context, new MockSessionStore(),
            ProfileManagerFactory.DEFAULT).get();
        assertEquals(VALUE, credentials.getToken());
    }

    @Test
    public void testRetrievePostParameterOk() {
        val context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(GOOD_PARAMETER, VALUE);
        val credentials = (TokenCredentials) postExtractor.extract(context, new MockSessionStore(),
            ProfileManagerFactory.DEFAULT).get();
        assertEquals(VALUE, credentials.getToken());
    }

    @Test
    public void testRetrievePostParameterNotSupported() {
        val context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name())
            .addRequestParameter(GOOD_PARAMETER, VALUE);
        TestsHelper.expectException(() -> getExtractor.extract(context, new MockSessionStore(),
                ProfileManagerFactory.DEFAULT), CredentialsException.class, "POST requests not supported");
    }

    @Test
    public void testRetrieveGetParameterNotSupported() {
        val context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.GET.name())
            .addRequestParameter(GOOD_PARAMETER, VALUE);
        TestsHelper.expectException(() -> postExtractor.extract(context, new MockSessionStore(),
                ProfileManagerFactory.DEFAULT), CredentialsException.class, "GET requests not supported");
    }

    @Test
    public void testRetrieveNoGetParameter() {
        val context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.GET.name());
        val credentials = getExtractor.extract(context, new MockSessionStore(),
            ProfileManagerFactory.DEFAULT);
        assertFalse(credentials.isPresent());
    }

    @Test
    public void testRetrieveNoPostParameter() {
        val context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name());
        val credentials = postExtractor.extract(context, new MockSessionStore(),
            ProfileManagerFactory.DEFAULT);
        assertFalse(credentials.isPresent());
    }
}
