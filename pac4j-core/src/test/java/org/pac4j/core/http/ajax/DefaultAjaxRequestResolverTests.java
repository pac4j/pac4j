package org.pac4j.core.http.ajax;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;

import static org.junit.Assert.*;

/**
 * Tests the {@link DefaultAjaxRequestResolver}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultAjaxRequestResolverTests {

    private final AjaxRequestResolver resolver = new DefaultAjaxRequestResolver();

    @Test
    public void testRealAjaxRequest() {
        final var context = MockWebContext.create().addRequestHeader("X-Requested-With", "XMLHttpRequest");
        assertTrue(resolver.isAjax(context, null));
    }

    @Test
    public void testForcedAjaxParameter() {
        final var context = MockWebContext.create().addRequestParameter("is_ajax_request", "true");
        assertTrue(resolver.isAjax(context, null));
    }

    @Test
    public void testForcedAjaxHeader() {
        final var context = MockWebContext.create().addRequestHeader("is_ajax_request", "true");
        assertTrue(resolver.isAjax(context, null));
    }

    @Test
    public void testNotAnAjaxRequest() {
        assertFalse(resolver.isAjax(MockWebContext.create(), null));
    }
}
