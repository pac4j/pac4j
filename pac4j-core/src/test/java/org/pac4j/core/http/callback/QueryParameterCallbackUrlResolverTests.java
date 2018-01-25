package org.pac4j.core.http.callback;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * Tests {@link QueryParameterCallbackUrlResolver}.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public final class QueryParameterCallbackUrlResolverTests implements TestsConstants {

    private static final QueryParameterCallbackUrlResolver resolver = new QueryParameterCallbackUrlResolver();

    @Test
    public void testCompute() {
        final String url = resolver.compute(new DefaultUrlResolver(), CALLBACK_URL, CLIENT_NAME, MockWebContext.create());
        assertEquals(CALLBACK_URL + "?" + Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER + "=" + CLIENT_NAME, url);
    }

    @Test
    public void testComputeSpecificParameter() {
        final QueryParameterCallbackUrlResolver resolver = new QueryParameterCallbackUrlResolver();
        resolver.setClientNameParameter(KEY);
        final String url = resolver.compute(new DefaultUrlResolver(), CALLBACK_URL, CLIENT_NAME, MockWebContext.create());
        assertEquals(CALLBACK_URL + "?" + KEY + "=" + CLIENT_NAME, url);
    }

    @Test
    public void testComputeCallbackUrlAlreadyDefined() {
        final String callbackUrl = CALLBACK_URL + "?" + Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER + "=cn";
        final String url = resolver.compute(new DefaultUrlResolver(), callbackUrl, CLIENT_NAME, MockWebContext.create());
        assertEquals(callbackUrl, url);
    }

    @Test
    public void testMatchesNoClientName() {
        assertFalse(resolver.matches(CLIENT_NAME, MockWebContext.create()));
    }

    @Test
    public void testMatches() {
        final MockWebContext context = MockWebContext.create();
        context.addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, CLIENT_NAME);
        assertTrue(resolver.matches(CLIENT_NAME, context));
    }
}
