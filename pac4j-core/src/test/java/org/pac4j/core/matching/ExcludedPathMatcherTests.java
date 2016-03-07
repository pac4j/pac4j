package org.pac4j.core.matching;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;

import java.util.regex.PatternSyntaxException;

import static org.junit.Assert.*;

/**
 * Tests {@link ExcludedPathMatcher}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class ExcludedPathMatcherTests {

    private ExcludedPathMatcher matcher = new ExcludedPathMatcher("^/(img/.*|css/.*|page\\.html)$");

    @Test
    public void testBlankPath() {
        final ExcludedPathMatcher pathMatcher = new ExcludedPathMatcher();
        assertTrue(pathMatcher.matches(MockWebContext.create().setPath("/page.html")));
        assertTrue(pathMatcher.matches(MockWebContext.create()));
    }

    @Test(expected = TechnicalException.class)
    public void testMissingStartCharacterInRegexp() {
        final ExcludedPathMatcher pathMatcher = new ExcludedPathMatcher("/img/.*$");
    }

    @Test(expected = TechnicalException.class)
    public void testMissingEndCharacterInRegexp() {
        final ExcludedPathMatcher pathMatcher = new ExcludedPathMatcher("^/img/.*");
    }

    @Test(expected = PatternSyntaxException.class)
    public void testBadRegexp() {
        final ExcludedPathMatcher pathMatcher = new ExcludedPathMatcher("^/img/**$");
    }

    @Test
    public void testNoPath() {
        final ExcludedPathMatcher pathMatcher = new ExcludedPathMatcher("^/$");
        assertFalse(pathMatcher.matches(MockWebContext.create().setPath("/")));
    }

    @Test
    public void testMatch() {
        assertTrue(matcher.matches(MockWebContext.create().setPath("/js/app.js")));
        assertTrue(matcher.matches(MockWebContext.create().setPath("/")));
        assertTrue(matcher.matches(MockWebContext.create().setPath("/page.htm")));
    }

    @Test
    public void testDontMatch() {
        assertFalse(matcher.matches(MockWebContext.create().setPath("/css/app.css")));
        assertFalse(matcher.matches(MockWebContext.create().setPath("/img/")));
        assertFalse(matcher.matches(MockWebContext.create().setPath("/page.html")));
    }
}
