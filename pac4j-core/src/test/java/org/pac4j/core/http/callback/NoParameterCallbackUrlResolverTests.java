package org.pac4j.core.http.callback;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * Tests {@link NoParameterCallbackUrlResolver}.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public final class NoParameterCallbackUrlResolverTests implements TestsConstants {

    private static final NoParameterCallbackUrlResolver resolver = new NoParameterCallbackUrlResolver();

    @Test
    public void testCompute() {
        assertEquals(CALLBACK_URL, resolver.compute(new DefaultUrlResolver(), CALLBACK_URL, MY_CLIENT_NAME, MockWebContext.create()));
    }

    @Test
    public void testMatches() {
        assertFalse(resolver.matches(MY_CLIENT_NAME, MockWebContext.create()));
    }
}
