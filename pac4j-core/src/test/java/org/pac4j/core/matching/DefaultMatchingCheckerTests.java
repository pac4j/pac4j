package org.pac4j.core.matching;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests {@link DefaultMatchingChecker}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class DefaultMatchingCheckerTests implements TestsConstants {

    private final static MatchingChecker checker = new DefaultMatchingChecker();

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
    public void testNoMatcherName() throws HttpAction {
        assertTrue(checker.matches(null, null, new HashMap<>()));
    }

    @Test
    public void testNoMatchers() throws HttpAction {
        TestsHelper.expectException(() -> checker.matches(null, NAME, null), TechnicalException.class, "matchersMap cannot be null");
    }

    @Test
    public void testNoExistingMatcher() throws HttpAction {
        TestsHelper.expectException(() -> checker.matches(null, NAME, new HashMap<>()), TechnicalException.class, "matchersMap['" + NAME + "'] cannot be null");
    }

    @Test
    public void testMatch() throws HttpAction {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        assertTrue(checker.matches(MockWebContext.create(), NAME, matchers));
    }

    @Test
    public void testMatchCasTrim() throws HttpAction {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        assertTrue(checker.matches(MockWebContext.create(), "  NAmE  ", matchers));
    }

    @Test
    public void testDontMatch() throws HttpAction {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        assertFalse(checker.matches(null, NAME, matchers));
    }

    @Test
    public void testMatchAll() throws HttpAction {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        matchers.put(VALUE, new NullContextMatcher());
        assertTrue(checker.matches(MockWebContext.create(), NAME + Pac4jConstants.ELEMENT_SEPRATOR + VALUE, matchers));
    }

    @Test
    public void testDontMatchOneOfThem() throws HttpAction {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        matchers.put(VALUE, new AlwaysFalseMatcher());
        assertFalse(checker.matches(MockWebContext.create(), NAME + Pac4jConstants.ELEMENT_SEPRATOR + VALUE, matchers));
    }
}
