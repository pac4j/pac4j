/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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

        final String result = resolver.compute("/cas/login", context);

        assertEquals("https://localhost/cas/login", result);
    }
}
