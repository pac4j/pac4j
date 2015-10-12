/*
 *    Copyright 2012 - 2015 pac4j organization
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.pac4j.core.context;

import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;

import java.util.regex.PatternSyntaxException;

import static org.junit.Assert.*;

/**
 * Tests {@link ExcludePathMatcher}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class ExcludePathMatcherTests {

    private final static String BEGIN_URL = "http://example.com";

    private ExcludePathMatcher matcher = new ExcludePathMatcher("^/(img/.*|css/.*|page\\.html)$");

    @Test
    public void testBlankPath() {
        final ExcludePathMatcher pathMatcher = new ExcludePathMatcher();
        assertFalse(pathMatcher.matches(MockWebContext.create().setFullRequestURL(BEGIN_URL + "/page.html")));
        assertFalse(pathMatcher.matches(MockWebContext.create()));
    }

    @Test(expected = TechnicalException.class)
    public void testMissingStartCharacterInRegexp() {
        final ExcludePathMatcher pathMatcher = new ExcludePathMatcher("/img/.*$");
    }

    @Test(expected = TechnicalException.class)
    public void testMissingEndCharacterInRegexp() {
        final ExcludePathMatcher pathMatcher = new ExcludePathMatcher("^/img/.*");
    }

    @Test(expected = PatternSyntaxException.class)
    public void testBadRegexp() {
        final ExcludePathMatcher pathMatcher = new ExcludePathMatcher("^/img/**$");
    }

    @Test
    public void testNoPath() {
        final ExcludePathMatcher pathMatcher = new ExcludePathMatcher("^/$");
        assertTrue(pathMatcher.matches(MockWebContext.create().setFullRequestURL(BEGIN_URL)));
    }

    @Test
    public void testDontMatch() {
        assertFalse(matcher.matches(MockWebContext.create().setFullRequestURL(BEGIN_URL + "/js/app.js")));
        assertFalse(matcher.matches(MockWebContext.create().setFullRequestURL(BEGIN_URL + "/")));
        assertFalse(matcher.matches(MockWebContext.create().setFullRequestURL(BEGIN_URL + "/page.htm")));
    }

    @Test
    public void testMatch() {
        assertTrue(matcher.matches(MockWebContext.create().setFullRequestURL(BEGIN_URL + "/css/app.css")));
        assertTrue(matcher.matches(MockWebContext.create().setFullRequestURL(BEGIN_URL + "/img/")));
        assertTrue(matcher.matches(MockWebContext.create().setFullRequestURL(BEGIN_URL + "/page.html")));
    }
}
