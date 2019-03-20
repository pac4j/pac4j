package org.pac4j.core.matching;

import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests {@link RequireAllMatchersChecker}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class RequireAllMatchersCheckerTests implements TestsConstants {

    private final static MatchingChecker checker = new RequireAllMatchersChecker();

    private static class NullContextMatcher implements Matcher {

        @Override
        public boolean matches(final WebContext context) {
            return context != null;
        }
    }

    private static class AlwaysFalseMatcher implements Matcher {

        @Override
        public boolean matches(final WebContext context) {
            return false;
        }
    }

    @Test
    public void testNoMatcherName() {
        assertTrue(checker.matches(null, null, new HashMap<>()));
    }

    @Test
    public void testNoMatchers() {
        TestsHelper.expectException(() -> checker.matches(null, NAME, null), TechnicalException.class, "matchersMap cannot be null");
    }

    @Test
    public void testNoExistingMatcher()  {
        TestsHelper.expectException(() -> checker.matches(null, NAME, new HashMap<>()), TechnicalException.class,
            "allMatchers['" + NAME + "'] cannot be null");
    }

    @Test
    public void testMatch() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        assertTrue(checker.matches(MockWebContext.create(), NAME, matchers));
    }

    @Test
    public void testMatchCasTrim() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        assertTrue(checker.matches(MockWebContext.create(), "  NAmE  ", matchers));
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


    @Test
    public void testDefaultGetMatcher() {
        final Map<String, Matcher> matchers = new HashMap<>();
        assertTrue(checker.matches(MockWebContext.create(), "get", matchers));
    }

    @Test
    public void testGetMatcherDefinedAsPost() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put("get", new HttpMethodMatcher(HttpConstants.HTTP_METHOD.POST));
        assertTrue(checker.matches(MockWebContext.create().setRequestMethod("post"), "get", matchers));
    }

    @Test
    public void testDefaultPostMatcher() {
        final Map<String, Matcher> matchers = new HashMap<>();
        assertTrue(checker.matches(MockWebContext.create().setRequestMethod("post"), "post", matchers));
    }

    @Test
    public void testDefaultPutMatcher() {
        final Map<String, Matcher> matchers = new HashMap<>();
        assertTrue(checker.matches(MockWebContext.create().setRequestMethod("put"), "put", matchers));
    }

    @Test
    public void testDefaultDeleteMatcher() {
        final Map<String, Matcher> matchers = new HashMap<>();
        assertTrue(checker.matches(MockWebContext.create().setRequestMethod("delete"), "delete", matchers));
    }
}
