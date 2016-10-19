package org.pac4j.core.matching;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.HttpAction;
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
    public void testNullHeaderName() throws HttpAction {
        final HeaderMatcher matcher = new HeaderMatcher();
        TestsHelper.expectException(() -> matcher.matches(MockWebContext.create()), TechnicalException.class, "headerName cannot be blank");
    }

    @Test
    public void testNullExpectedValueHeader() throws HttpAction {
        final HeaderMatcher matcher = new HeaderMatcher(NAME, null);
        final MockWebContext context = MockWebContext.create().addRequestHeader(NAME, VALUE);
        assertFalse(matcher.matches(context));
    }

    @Test
    public void testNullExpectedValueNull() throws HttpAction {
        final HeaderMatcher matcher = new HeaderMatcher(NAME, null);
        final MockWebContext context = MockWebContext.create();
        assertTrue(matcher.matches(context));
    }

    @Test
    public void testRegexExpectedRightValueHeader() throws HttpAction {
        final HeaderMatcher matcher = new HeaderMatcher(NAME, ".*A.*");
        final MockWebContext context = MockWebContext.create().addRequestHeader(NAME, "BAC");
        assertTrue(matcher.matches(context));
    }

    @Test
    public void testRegexExpectedBadValueHeader() throws HttpAction {
        final HeaderMatcher matcher = new HeaderMatcher(NAME, ".*A.*");
        final MockWebContext context = MockWebContext.create().addRequestHeader(NAME, "BOC");
        assertFalse(matcher.matches(context));
    }

    @Test
    public void testRegexExpectedNullHeader() throws HttpAction {
        final HeaderMatcher matcher = new HeaderMatcher(NAME, ".*A.*");
        final MockWebContext context = MockWebContext.create();
        assertFalse(matcher.matches(context));
    }
}
