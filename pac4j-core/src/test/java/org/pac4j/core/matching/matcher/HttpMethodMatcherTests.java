package org.pac4j.core.matching.matcher;

import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static org.junit.Assert.*;

/**
 * Tests {@link HttpMethodMatcher}.
 *
 * @author Jerome Leleu
 * @since 1.9.3
 */
public final class HttpMethodMatcherTests implements TestsConstants {

    @Test
    public void testNullMethods() {
        final HttpMethodMatcher matcher = new HttpMethodMatcher();
        TestsHelper.expectException(() -> matcher.matches(MockWebContext.create(), null),
            TechnicalException.class, "methods cannot be null");
    }

    @Test
    public void testBadMethod() {
        final HttpMethodMatcher matcher = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.GET);
        final MockWebContext context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name());
        assertFalse(matcher.matches(context, null));
    }

    @Test
    public void testGoodMethod() {
        final HttpMethodMatcher matcher = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.POST);
        final MockWebContext context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name());
        assertTrue(matcher.matches(context, null));
    }

    @Test
    public void testBadMethod2() {
        final HttpMethodMatcher matcher = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.GET, HttpConstants.HTTP_METHOD.PUT);
        final MockWebContext context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name());
        assertFalse(matcher.matches(context, null));
    }

    @Test
    public void testGoodMethod2() {
        final HttpMethodMatcher matcher = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.DELETE, HttpConstants.HTTP_METHOD.POST);
        final MockWebContext context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name());
        assertTrue(matcher.matches(context, null));
    }
}
