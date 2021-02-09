package org.pac4j.core.matching.matcher;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
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
        final var matcher = new HeaderMatcher();
        TestsHelper.expectException(() -> matcher.matches(MockWebContext.create(), new MockSessionStore()),
            TechnicalException.class, "headerName cannot be blank");
    }

    @Test
    public void testNullExpectedValueHeader() {
        final var matcher = new HeaderMatcher(NAME, null);
        final var context = MockWebContext.create().addRequestHeader(NAME, VALUE);
        assertFalse(matcher.matches(context, new MockSessionStore()));
    }

    @Test
    public void testNullExpectedValueNull() {
        final var matcher = new HeaderMatcher(NAME, null);
        final var context = MockWebContext.create();
        assertTrue(matcher.matches(context, new MockSessionStore()));
    }

    @Test
    public void testRegexExpectedRightValueHeader() {
        final var matcher = new HeaderMatcher(NAME, ".*A.*");
        final var context = MockWebContext.create().addRequestHeader(NAME, "BAC");
        assertTrue(matcher.matches(context, new MockSessionStore()));
    }

    @Test
    public void testRegexExpectedBadValueHeader() {
        final var matcher = new HeaderMatcher(NAME, ".*A.*");
        final var context = MockWebContext.create().addRequestHeader(NAME, "BOC");
        assertFalse(matcher.matches(context, new MockSessionStore()));
    }

    @Test
    public void testRegexExpectedNullHeader() {
        final var matcher = new HeaderMatcher(NAME, ".*A.*");
        final var context = MockWebContext.create();
        assertFalse(matcher.matches(context, new MockSessionStore()));
    }
}
