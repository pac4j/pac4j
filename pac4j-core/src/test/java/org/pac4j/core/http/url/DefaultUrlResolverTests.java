package org.pac4j.core.http.url;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link DefaultUrlResolver}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class DefaultUrlResolverTests implements TestsConstants {

    private final UrlResolver resolver = new DefaultUrlResolver(true);

    @Test
    public void testComputePassthrough() {
        val context = MockWebContext.create();
        context.setServerName("pac4j.com");

        val result = new DefaultUrlResolver().compute(PATH, context);

        assertEquals(PATH, result);
    }

    @Test
    public void testCompute_whenHostIsNotPresent() {
        val context = MockWebContext.create();
        context.setServerName("pac4j.com");

        val result = resolver.compute("/cas/login", context);

        assertEquals("http://pac4j.com/cas/login", result);
    }

    @Test
    public void testCompute_whenHostIsPresent() {
        val context = MockWebContext.create();
        context.setServerName("pac4j.com");

        val result = resolver.compute("http://cashost.com/cas/login", context);

        assertEquals("http://cashost.com/cas/login", result);
    }

    @Test
    public void testCompute_whenServerIsNotUsingDefaultHttpPort() {
        val context = MockWebContext.create();
        context.setServerName("pac4j.com");
        context.setServerPort(8080);

        val result = resolver.compute("/cas/login", context);

        assertEquals("http://pac4j.com:8080/cas/login", result);
    }

    @Test
    public void testCompute_whenRequestIsSecure() {
        val context = MockWebContext.create();
        context.setScheme("https");
        context.setSecure(true);
        context.setServerPort(443);

        val result = resolver.compute("/cas/login", context);

        assertEquals("https://localhost/cas/login", result);
    }

    @Test
    public void testCompute_whenServerIsNotUsingDefaultHttpsPort() {
        val context = MockWebContext.create();
        context.setServerName("pac4j.com");
        context.setScheme("https");
        context.setSecure(true);
        context.setServerPort(8181);

        val result = resolver.compute("/cas/login", context);

        assertEquals("https://pac4j.com:8181/cas/login", result);
    }
}
