package org.pac4j.core.matching;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsHelper;

import java.util.regex.PatternSyntaxException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link PathMatcher}.
 *
 * @author Rob Ward
 * @since 2.0.0
 */
public class PathMatcherTests {

    @Test
    public void testBlankPath() {
        final PathMatcher pathMatcher = new PathMatcher();
        assertTrue(pathMatcher.matches(MockWebContext.create().setPath("/page.html")));
        assertTrue(pathMatcher.matches(MockWebContext.create()));
    }

    @Test
    public void testFixedPath() {
        final PathMatcher pathMatcher = new PathMatcher().addExcludedPath("/foo");
        assertFalse(pathMatcher.matches(MockWebContext.create().setPath("/foo")));
        assertTrue(pathMatcher.matches(MockWebContext.create().setPath("/foo/bar")));
    }

    @Test
    public void testBranch() {
        final PathMatcher pathMatcher = new PathMatcher().addExcludedBranch("/foo");
        assertFalse(pathMatcher.matches(MockWebContext.create().setPath("/foo")));
        assertFalse(pathMatcher.matches(MockWebContext.create().setPath("/foo/")));
        assertFalse(pathMatcher.matches(MockWebContext.create().setPath("/foo/bar")));
    }

    @Test
    public void testMissingStartCharacterInRegexp() {
        TestsHelper.expectException(() -> new PathMatcher().addExcludedRegex("/img/.*$"), TechnicalException.class,
                "Your regular expression: '/img/.*$' must start with a ^ and end with a $ to define a full path matching");
    }

    @Test
    public void testMissingEndCharacterInRegexp() {
        TestsHelper.expectException(() -> new PathMatcher().addExcludedRegex("^/img/.*"), TechnicalException.class, "Your regular expression: '^/img/.*' must start with a ^ and end with a $ to define a full path matching");
    }

    @Test(expected = PatternSyntaxException.class)
    public void testBadRegexp() {
        new PathMatcher().addExcludedRegex("^/img/**$");
    }

    @Test
    public void testNoPath() {
        final PathMatcher pathMatcher = new PathMatcher().addExcludedRegex("^/$");
        assertFalse(pathMatcher.matches(MockWebContext.create().setPath("/")));
    }

    @Test
    public void testMatch() {
        final PathMatcher matcher = new PathMatcher().addExcludedRegex("^/(img/.*|css/.*|page\\.html)$");
        assertTrue(matcher.matches(MockWebContext.create().setPath("/js/app.js")));
        assertTrue(matcher.matches(MockWebContext.create().setPath("/")));
        assertTrue(matcher.matches(MockWebContext.create().setPath("/page.htm")));
    }

    @Test
    public void testDontMatch() {
        final PathMatcher matcher = new PathMatcher().addExcludedRegex("^/(img/.*|css/.*|page\\.html)$");
        assertFalse(matcher.matches(MockWebContext.create().setPath("/css/app.css")));
        assertFalse(matcher.matches(MockWebContext.create().setPath("/img/")));
        assertFalse(matcher.matches(MockWebContext.create().setPath("/page.html")));
    }
}
