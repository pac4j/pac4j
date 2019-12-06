package org.pac4j.core.matching.matcher;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.*;

/**
 * Tests {@link HeaderMatcher}.
 *
 * @author Jerome Leleu
 * @since 1.9.3
 */
public final class HeaderMatcherTests implements TestsConstants {

    @Test
    public void testNullHeaderName() {
        final HeaderMatcher matcher = new HeaderMatcher();
        TestsHelper.expectException(() -> matcher.matches(MockWebContext.create()), TechnicalException.class, "headerName cannot be blank");
    }

    @Test
    public void testNullExpectedValueHeader() {
        final HeaderMatcher matcher = new HeaderMatcher(NAME, null);
        final MockWebContext context = MockWebContext.create().addRequestHeader(NAME, VALUE);
        assertFalse(matcher.matches(context));
    }

    @Test
    public void testNullExpectedValueNull() {
        final HeaderMatcher matcher = new HeaderMatcher(NAME, null);
        final MockWebContext context = MockWebContext.create();
        assertTrue(matcher.matches(context));
    }

    @Test
    public void testRegexExpectedRightValueHeader() {
        final HeaderMatcher matcher = new HeaderMatcher(NAME, ".*A.*");
        final MockWebContext context = MockWebContext.create().addRequestHeader(NAME, "BAC");
        assertTrue(matcher.matches(context));
    }

    @Test
    public void testRegexExpectedBadValueHeader() {
        final HeaderMatcher matcher = new HeaderMatcher(NAME, ".*A.*");
        final MockWebContext context = MockWebContext.create().addRequestHeader(NAME, "BOC");
        assertFalse(matcher.matches(context));
    }

    @Test
    public void testRegexExpectedNullHeader() {
        final HeaderMatcher matcher = new HeaderMatcher(NAME, ".*A.*");
        final MockWebContext context = MockWebContext.create();
        assertFalse(matcher.matches(context));
    }
}
