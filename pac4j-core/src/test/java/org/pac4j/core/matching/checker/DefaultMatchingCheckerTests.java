package org.pac4j.core.matching.checker;

import org.junit.Test;
import org.pac4j.core.context.*;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.matching.matcher.DefaultMatchers;
import org.pac4j.core.matching.matcher.HttpMethodMatcher;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.pac4j.core.context.HttpConstants.*;

/**
 * Tests {@link DefaultMatchingChecker}.
 *
 * @author Jerome Leleu
 * @since 4.0.0
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
    public void testNoMatcherName() {
        final MockWebContext context = MockWebContext.create();
        assertTrue(checker.matches(context, null, new HashMap<>()));
        assertEquals(6, context.getResponseHeaders().size());
    }

    @Test
    public void testNoneMatcherName() {
        final MockWebContext context = MockWebContext.create();
        assertTrue(checker.matches(context, "  NoNe   ", new HashMap<>()));
        assertEquals(0, context.getResponseHeaders().size());
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
        assertTrue(checker.matches(MockWebContext.create(), NAME + Pac4jConstants.ELEMENT_SEPARATOR + VALUE, matchers));
    }

    @Test
    public void testDontMatchOneOfThem() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        matchers.put(VALUE, new AlwaysFalseMatcher());
        assertFalse(checker.matches(MockWebContext.create(), NAME + Pac4jConstants.ELEMENT_SEPARATOR + VALUE, matchers));
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

    @Test
    public void testHsts() {
        final MockWebContext context = MockWebContext.create();
        context.setScheme(SCHEME_HTTPS);
        checker.matches(context, DefaultMatchers.HSTS, new HashMap<>());
        assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
    }

    @Test
    public void testHstsCaseTrim() {
        final MockWebContext context = MockWebContext.create();
        context.setScheme(SCHEME_HTTPS);
        checker.matches(context, "  HSTS ", new HashMap<>());
        assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
    }

    @Test
    public void testNosniff() {
        final MockWebContext context = MockWebContext.create();
        checker.matches(context, DefaultMatchers.NOSNIFF, new HashMap<>());
        assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
    }

    @Test
    public void testNoframe() {
        final MockWebContext context = MockWebContext.create();
        checker.matches(context, DefaultMatchers.NOFRAME, new HashMap<>());
        assertNotNull(context.getResponseHeaders().get("X-Frame-Options"));
    }

    @Test
    public void testXssprotection() {
        final MockWebContext context = MockWebContext.create();
        checker.matches(context, DefaultMatchers.XSSPROTECTION, new HashMap<>());
        assertNotNull(context.getResponseHeaders().get("X-XSS-Protection"));
    }

    @Test
    public void testNocache() {
        final MockWebContext context = MockWebContext.create();
        checker.matches(context, DefaultMatchers.NOCACHE, new HashMap<>());
        assertNotNull(context.getResponseHeaders().get("Cache-Control"));
        assertNotNull(context.getResponseHeaders().get("Pragma"));
        assertNotNull(context.getResponseHeaders().get("Expires"));
    }

    @Test
    public void testAllowAjaxRequests() {
        final MockWebContext context = MockWebContext.create();
        checker.matches(context, DefaultMatchers.ALLOW_AJAX_REQUESTS, new HashMap<>());
        assertEquals("*", context.getResponseHeaders().get(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER));
        assertEquals("true", context.getResponseHeaders().get(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER));
        final String methods = context.getResponseHeaders().get(ACCESS_CONTROL_ALLOW_METHODS_HEADER);
        final List<String> methodArray = Arrays.asList(methods.split(",")).stream().map(String::trim).collect(Collectors.toList());
        assertTrue(methodArray.contains(HTTP_METHOD.POST.name()));
        assertTrue(methodArray.contains(HTTP_METHOD.PUT.name()));
        assertTrue(methodArray.contains(HTTP_METHOD.DELETE.name()));
        assertTrue(methodArray.contains(HTTP_METHOD.OPTIONS.name()));
        assertTrue(methodArray.contains(HTTP_METHOD.GET.name()));
    }

    @Test
    public void testSecurityHeaders() {
        final MockWebContext context = MockWebContext.create();
        context.setScheme(SCHEME_HTTPS);
        checker.matches(context, DefaultMatchers.SECURITYHEADERS, new HashMap<>());
        assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
        assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
        assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
        assertNotNull(context.getResponseHeaders().get("X-XSS-Protection"));
        assertNotNull(context.getResponseHeaders().get("Cache-Control"));
        assertNotNull(context.getResponseHeaders().get("Pragma"));
        assertNotNull(context.getResponseHeaders().get("Expires"));
    }

    @Test
    public void testCsrfToken() {
        final MockWebContext context = MockWebContext.create();
        assertTrue(checker.matches(context, DefaultMatchers.CSRF_TOKEN, new HashMap<>()));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfTokenPost() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HTTP_METHOD.POST.name());
        assertTrue(checker.matches(context, DefaultMatchers.CSRF_TOKEN, new HashMap<>()));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }
}
