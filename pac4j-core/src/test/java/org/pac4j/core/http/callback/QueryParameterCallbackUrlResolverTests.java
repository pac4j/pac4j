package org.pac4j.core.http.callback;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

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
        final String url = new QueryParameterCallbackUrlResolver(ImmutableMap.of("param1", "value", "param2", "value2"))
            .compute(new DefaultUrlResolver(), CALLBACK_URL, CLIENT_NAME, MockWebContext.create());
        assertEquals(CALLBACK_URL +'?' + Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER
            + '=' + CLIENT_NAME + "&param1=value&param2=value2", url);
    }

    @Test
    public void testParamsFromConfig() {
        final BaseClientConfiguration clientConfiguration = mock(BaseClientConfiguration.class);
        doReturn(ImmutableMap.of("TestParam", "testValue")).when(clientConfiguration).getCustomParams();

        final String url = new QueryParameterCallbackUrlResolver(clientConfiguration)
            .compute(new DefaultUrlResolver(), CALLBACK_URL, CLIENT_NAME, MockWebContext.create());
        assertEquals(CALLBACK_URL +'?' + Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER
            + '=' + CLIENT_NAME + "&TestParam=testValue", url);
    }

    @Test
    public void testParamsFromConfigNull() {
        final BaseClientConfiguration clientConfiguration = mock(BaseClientConfiguration.class);
        doReturn(null).when(clientConfiguration).getCustomParams();

        final String url = new QueryParameterCallbackUrlResolver(clientConfiguration)
            .compute(new DefaultUrlResolver(), CALLBACK_URL, CLIENT_NAME, MockWebContext.create());
        assertEquals(CALLBACK_URL +'?' + Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER
            + '=' + CLIENT_NAME, url);
    }

    @Test
    public void testCompute() {
        final String url = resolver.compute(new DefaultUrlResolver(), CALLBACK_URL, CLIENT_NAME, MockWebContext.create());
        assertEquals(CALLBACK_URL +'?' + Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER + '=' + CLIENT_NAME, url);
    }

    @Test
    public void testComputeSpecificParameter() {
        final QueryParameterCallbackUrlResolver resolver = new QueryParameterCallbackUrlResolver();
        resolver.setClientNameParameter(KEY);
        final String url = resolver.compute(new DefaultUrlResolver(), CALLBACK_URL, CLIENT_NAME, MockWebContext.create());
        assertEquals(CALLBACK_URL +'?' + KEY + '=' + CLIENT_NAME, url);
    }

    @Test
    public void testComputeCallbackUrlAlreadyDefined() {
        final String callbackUrl = CALLBACK_URL +'?' + Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER + "=cn";
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
