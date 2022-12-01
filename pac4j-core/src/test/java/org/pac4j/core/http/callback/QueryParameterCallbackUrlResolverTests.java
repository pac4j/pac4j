package org.pac4j.core.http.callback;

import com.google.common.collect.ImmutableMap;
import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.util.Pac4jConstants;
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
    public void testParams() {
        val url = new QueryParameterCallbackUrlResolver(ImmutableMap.of("param1", "value", "param2", "value2"))
            .compute(new DefaultUrlResolver(), CALLBACK_URL, MY_CLIENT_NAME, MockWebContext.create());
        assertEquals(CALLBACK_URL +'?' + Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER
            + '=' + MY_CLIENT_NAME + "&param1=value&param2=value2", url);
    }
    @Test
    public void testCompute() {
        val url = resolver.compute(new DefaultUrlResolver(), CALLBACK_URL, MY_CLIENT_NAME, MockWebContext.create());
        assertEquals(CALLBACK_URL +'?' + Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER + '=' + MY_CLIENT_NAME, url);
    }

    @Test
    public void testComputeSpecificParameter() {
        val resolver = new QueryParameterCallbackUrlResolver();
        resolver.setClientNameParameter(KEY);
        val url = resolver.compute(new DefaultUrlResolver(), CALLBACK_URL, MY_CLIENT_NAME, MockWebContext.create());
        assertEquals(CALLBACK_URL +'?' + KEY + '=' + MY_CLIENT_NAME, url);
    }

    @Test
    public void testComputeCallbackUrlAlreadyDefined() {
        val callbackUrl = CALLBACK_URL +'?' + Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER + "=cn";
        val url = resolver.compute(new DefaultUrlResolver(), callbackUrl, MY_CLIENT_NAME, MockWebContext.create());
        assertEquals(callbackUrl, url);
    }

    @Test
    public void testMatchesNoClientName() {
        assertFalse(resolver.matches(MY_CLIENT_NAME, MockWebContext.create()));
    }

    @Test
    public void testMatches() {
        val context = MockWebContext.create();
        context.addRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER, MY_CLIENT_NAME);
        assertTrue(resolver.matches(MY_CLIENT_NAME, context));
    }
}
