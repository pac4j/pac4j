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
package org.pac4j.core.matching;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests {@link DefaultMatchingChecker}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class DefaultMatchingCheckerTests implements TestsConstants {

    private final static MatchingChecker checker = new DefaultMatchingChecker();

    class NullContextMatcher implements Matcher {

        @Override
        public boolean matches(final WebContext context) {
            return context != null;
        }
    }

    class AlwaysFalseMatcher implements Matcher {

        @Override
        public boolean matches(final WebContext context) {
            return false;
        }
    }

    @Test
    public void testNoMatcherName() {
        assertTrue(checker.matches(null, null, new HashMap<String, Matcher>()));
    }

    @Test(expected = TechnicalException.class)
    public void testNoMatchers() {
        assertTrue(checker.matches(null, NAME, null));
    }

    @Test(expected = TechnicalException.class)
    public void testNoExistingMatcher() {
        assertTrue(checker.matches(null, NAME, new HashMap<String, Matcher>()));
    }

    @Test
    public void testMatch() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        assertTrue(checker.matches(MockWebContext.create(), NAME, matchers));
    }

    @Test
    public void testDontMatch() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        assertFalse(checker.matches(null, NAME, matchers));
    }

    @Test
    public void testMatchAll() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        matchers.put(VALUE, new NullContextMatcher());
        assertTrue(checker.matches(MockWebContext.create(), NAME + Pac4jConstants.ELEMENT_SEPRATOR + VALUE, matchers));
    }

    @Test
    public void testDontMatchOneOfThem() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        matchers.put(VALUE, new AlwaysFalseMatcher());
        assertFalse(checker.matches(MockWebContext.create(), NAME + Pac4jConstants.ELEMENT_SEPRATOR + VALUE, matchers));
    }
}
