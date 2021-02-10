package org.pac4j.core.matching.checker;

import org.junit.Test;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.context.*;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.matching.matcher.DefaultMatchers;
import org.pac4j.core.matching.matcher.HttpMethodMatcher;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.*;
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

    private final static DefaultMatchingChecker checker = new DefaultMatchingChecker();

    private static class NullContextMatcher implements Matcher {

        @Override
        public boolean matches(final WebContext context, final SessionStore sessionStore) {
            return context != null;
        }
    }

    private static class AlwaysFalseMatcher implements Matcher {

        @Override
        public boolean matches(final WebContext context, final SessionStore sessionStore) {
            return false;
        }
    }

    @Test
    public void testNoMatcherName() {
        final var context = MockWebContext.create();
        assertTrue(checker.matches(context, new MockSessionStore(), null, new HashMap<>(), new ArrayList<>()));
        assertEquals(6, context.getResponseHeaders().size());
    }

    @Test
    public void testNoneMatcherName() {
        final var context = MockWebContext.create();
        assertTrue(checker.matches(context, new MockSessionStore(), "  NoNe   ", new HashMap<>(), new ArrayList<>()));
        assertEquals(0, context.getResponseHeaders().size());
    }

    @Test
    public void testNoMatchers() {
        TestsHelper.expectException(() -> checker.matches(null, new MockSessionStore(), NAME, null, new ArrayList<>()),
            TechnicalException.class, "matchersMap cannot be null");
    }

    @Test
    public void testNoExistingMatcher()  {
        TestsHelper.expectException(() -> checker.matches(null, new MockSessionStore(), NAME, new HashMap<>(), new ArrayList<>()),
            TechnicalException.class, "The matcher '" + NAME + "' must be defined in the security configuration");
    }

    @Test
    public void testMatch() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        assertTrue(checker.matches(MockWebContext.create(), new MockSessionStore(), NAME, matchers, new ArrayList<>()));
    }

    @Test
    public void testMatchCasTrim() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        assertTrue(checker.matches(MockWebContext.create(), new MockSessionStore(), "  NAmE  ", matchers, new ArrayList<>()));
    }

    @Test
    public void testDontMatch() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        assertFalse(checker.matches(null, new MockSessionStore(), NAME, matchers, new ArrayList<>()));
    }

    @Test
    public void testMatchAll() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        matchers.put(VALUE, new NullContextMatcher());
        assertTrue(checker.matches(MockWebContext.create(), new MockSessionStore(), NAME + Pac4jConstants.ELEMENT_SEPARATOR + VALUE,
            matchers, new ArrayList<>()));
    }

    @Test
    public void testDontMatchOneOfThem() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(NAME, new NullContextMatcher());
        matchers.put(VALUE, new AlwaysFalseMatcher());
        assertFalse(checker.matches(MockWebContext.create(), new MockSessionStore(), NAME + Pac4jConstants.ELEMENT_SEPARATOR + VALUE,
            matchers, new ArrayList<>()));
    }

    @Test
    public void testDefaultGetMatcher() {
        final Map<String, Matcher> matchers = new HashMap<>();
        assertTrue(checker.matches(MockWebContext.create(), new MockSessionStore(), "get", matchers, new ArrayList<>()));
    }

    @Test
    public void testGetMatcherDefinedAsPost() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put("get", new HttpMethodMatcher(HttpConstants.HTTP_METHOD.POST));
        assertTrue(checker.matches(MockWebContext.create().setRequestMethod("post"), new MockSessionStore(), "get",
            matchers, new ArrayList<>()));
    }

    @Test
    public void testDefaultPostMatcher() {
        final Map<String, Matcher> matchers = new HashMap<>();
        assertTrue(checker.matches(MockWebContext.create().setRequestMethod("post"), new MockSessionStore(), "post",
            matchers, new ArrayList<>()));
    }

    @Test
    public void testDefaultPutMatcher() {
        final Map<String, Matcher> matchers = new HashMap<>();
        assertTrue(checker.matches(MockWebContext.create().setRequestMethod("put"), new MockSessionStore(), "put",
            matchers, new ArrayList<>()));
    }

    @Test
    public void testDefaultDeleteMatcher() {
        final Map<String, Matcher> matchers = new HashMap<>();
        assertTrue(checker.matches(MockWebContext.create().setRequestMethod("delete"), new MockSessionStore(), "delete",
            matchers, new ArrayList<>()));
    }

    @Test
    public void testHsts() {
        final var context = MockWebContext.create();
        context.setScheme(SCHEME_HTTPS);
        checker.matches(context, new MockSessionStore(), DefaultMatchers.HSTS, new HashMap<>(), new ArrayList<>());
        assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
    }

    @Test
    public void testHstsCaseTrim() {
        final var context = MockWebContext.create();
        context.setScheme(SCHEME_HTTPS);
        checker.matches(context, new MockSessionStore(), "  HSTS ", new HashMap<>(), new ArrayList<>());
        assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
    }

    @Test
    public void testNosniff() {
        final var context = MockWebContext.create();
        checker.matches(context, new MockSessionStore(), DefaultMatchers.NOSNIFF, new HashMap<>(), new ArrayList<>());
        assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
    }

    @Test
    public void testNoframe() {
        final var context = MockWebContext.create();
        checker.matches(context, new MockSessionStore(), DefaultMatchers.NOFRAME, new HashMap<>(), new ArrayList<>());
        assertNotNull(context.getResponseHeaders().get("X-Frame-Options"));
    }

    @Test
    public void testXssprotection() {
        final var context = MockWebContext.create();
        checker.matches(context, new MockSessionStore(), DefaultMatchers.XSSPROTECTION, new HashMap<>(), new ArrayList<>());
        assertNotNull(context.getResponseHeaders().get("X-XSS-Protection"));
    }

    @Test
    public void testNocache() {
        final var context = MockWebContext.create();
        checker.matches(context, new MockSessionStore(), DefaultMatchers.NOCACHE, new HashMap<>(), new ArrayList<>());
        assertNotNull(context.getResponseHeaders().get("Cache-Control"));
        assertNotNull(context.getResponseHeaders().get("Pragma"));
        assertNotNull(context.getResponseHeaders().get("Expires"));
    }

    @Test
    public void testAllowAjaxRequests() {
        final var context = MockWebContext.create();
        checker.matches(context, new MockSessionStore(), DefaultMatchers.ALLOW_AJAX_REQUESTS, new HashMap<>(), new ArrayList<>());
        assertEquals("*", context.getResponseHeaders().get(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER));
        assertEquals("true", context.getResponseHeaders().get(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER));
        final var methods = context.getResponseHeaders().get(ACCESS_CONTROL_ALLOW_METHODS_HEADER);
        final var methodArray = Arrays.asList(methods.split(",")).stream().map(String::trim).collect(Collectors.toList());
        assertTrue(methodArray.contains(HTTP_METHOD.POST.name()));
        assertTrue(methodArray.contains(HTTP_METHOD.PUT.name()));
        assertTrue(methodArray.contains(HTTP_METHOD.DELETE.name()));
        assertTrue(methodArray.contains(HTTP_METHOD.OPTIONS.name()));
        assertTrue(methodArray.contains(HTTP_METHOD.GET.name()));
    }

    @Test
    public void testSecurityHeaders() {
        final var context = MockWebContext.create();
        context.setScheme(SCHEME_HTTPS);
        checker.matches(context, new MockSessionStore(), DefaultMatchers.SECURITYHEADERS, new HashMap<>(), new ArrayList<>());
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
        final var context = MockWebContext.create();
        assertTrue(checker.matches(context, new MockSessionStore(), DefaultMatchers.CSRF_TOKEN, new HashMap<>(), new ArrayList<>()));
        assertTrue(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN).isPresent());
        assertNotNull(WebContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfTokenDefault() {
        final var context = MockWebContext.create();
        assertTrue(checker.matches(context, new MockSessionStore(), "", new HashMap<>(), new ArrayList<>()));
        assertFalse(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN).isPresent());
        assertNull(WebContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfTokenDefaultButSessionAlreadyExists() {
        final var context = MockWebContext.create();
        final SessionStore sessionStore = new MockSessionStore();
        sessionStore.getSessionId(context, true);
        assertTrue(checker.matches(context, sessionStore, "", new HashMap<>(), new ArrayList<>()));
        assertTrue(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN).isPresent());
        assertNotNull(WebContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfTokenDefaultButIndirectClient() {
        final var context = MockWebContext.create();
        final List<Client> clients = new ArrayList<>();
        clients.add(new MockIndirectClient("test"));
        assertTrue(checker.matches(context, new MockSessionStore(), "", new HashMap<>(), clients));
        assertTrue(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN).isPresent());
        assertNotNull(WebContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfTokenPost() {
        final var context = MockWebContext.create().setRequestMethod(HTTP_METHOD.POST.name());
        assertTrue(checker.matches(context, new MockSessionStore(), DefaultMatchers.CSRF_TOKEN, new HashMap<>(), new ArrayList<>()));
        assertTrue(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN).isPresent());
        assertNotNull(WebContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testComputeMatchers() {
        assertEquals(DefaultMatchingChecker.SECURITY_HEADERS_MATCHERS,
            checker.computeMatchers(MockWebContext.create(), new MockSessionStore(), "" , new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testComputeMatchersPost() {
        assertEquals(Arrays.asList(DefaultMatchingChecker.POST_MATCHER),
            checker.computeMatchers(MockWebContext.create(), new MockSessionStore(), "post" , new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testComputeMatchersPlusPost() {
        final List<Matcher> matchers = new ArrayList<>();
        matchers.addAll(DefaultMatchingChecker.SECURITY_HEADERS_MATCHERS);
        matchers.add(DefaultMatchingChecker.POST_MATCHER);
        assertEquals(matchers, checker.computeMatchers(MockWebContext.create(), new MockSessionStore(), "   +   post",
            new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testComputeMatchersOverrideDefault() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(DefaultMatchers.GET, DefaultMatchingChecker.POST_MATCHER);
        assertEquals(Arrays.asList(DefaultMatchingChecker.POST_MATCHER), checker.computeMatchers(MockWebContext.create(),
            new MockSessionStore(), "get", matchers, new ArrayList<>()));
    }
}
