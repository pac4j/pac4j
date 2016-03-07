package org.pac4j.core.http;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;

import static org.junit.Assert.*;

/**
 * Tests the {@link RelativeCallbackUrlResolver}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class RelativeCallbackUrlResolverTests {

    private final CallbackUrlResolver resolver = new RelativeCallbackUrlResolver();

    @Test
    public void testCompute_whenHostIsNotPresent() {
        final MockWebContext context = MockWebContext.create();
        context.setServerName("pac4j.com");

        final String result = resolver.compute("/cas/login", context);

        assertEquals("http://pac4j.com/cas/login", result);
    }

    @Test
    public void testCompute_whenHostIsPresent() {
        final MockWebContext context = MockWebContext.create();
        context.setServerName("pac4j.com");

        final String result = resolver.compute("http://cashost.com/cas/login", context);

        assertEquals("http://cashost.com/cas/login", result);
    }

    @Test
    public void testCompute_whenServerIsNotUsingDefaultHttpPort() {
        final MockWebContext context = MockWebContext.create();
        context.setServerName("pac4j.com");
        context.setServerPort(8080);

        final String result = resolver.compute("/cas/login", context);

        assertEquals("http://pac4j.com:8080/cas/login", result);
    }

    @Test
    public void testCompute_whenRequestIsSecure() {
        final MockWebContext context = MockWebContext.create();
        context.setScheme("https");
        context.setSecure(true);
        context.setServerPort(443);

        final String result = resolver.compute("/cas/login", context);

        assertEquals("https://localhost/cas/login", result);
    }
    
    @Test
    public void testCompute_whenServerIsNotUsingDefaultHttpsPort() {
        final MockWebContext context = MockWebContext.create();
        context.setServerName("pac4j.com");
        context.setScheme("https");
        context.setSecure(true);
        context.setServerPort(8181);

        final String result = resolver.compute("/cas/login", context);

        assertEquals("https://pac4j.com:8181/cas/login", result);
    }
}
