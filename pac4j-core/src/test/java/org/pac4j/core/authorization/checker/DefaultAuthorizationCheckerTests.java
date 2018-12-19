package org.pac4j.core.authorization.checker;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.authorization.authorizer.csrf.DefaultCsrfTokenGenerator;
import org.pac4j.core.context.*;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.BasicUserProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

import static org.pac4j.core.context.HttpConstants.*;

/**
 * Tests the {@link DefaultAuthorizationChecker}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultAuthorizationCheckerTests implements TestsConstants {

    private final DefaultAuthorizationChecker checker = new DefaultAuthorizationChecker();

    private List<UserProfile> profiles;

    private BasicUserProfile profile;

    @Before
    public void setUp() {
        profile = new BasicUserProfile();
        profiles = new ArrayList<>();
        profiles.add(profile);
    }

    private static class IdAuthorizer implements Authorizer<UserProfile> {
        @Override
        public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles) {
            return VALUE.equals(profiles.get(0).getId());
        }
    }

    @Test
    public void testBlankAuthorizerNameAProfile() {
        assertTrue(checker.isAuthorized(null, profiles, null, null));
    }

    @Test
    public void testOneExistingAuthorizerProfileMatch() {
        profile.setId(VALUE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        assertTrue(checker.isAuthorized(null, profiles, NAME, authorizers));
    }

    @Test
    public void testOneExistingAuthorizerProfileDoesNotMatch() {
        internalTestOneExistingAuthorizerProfileDoesNotMatch(NAME);
    }

    @Test
    public void testOneExistingAuthorizerProfileDoesNotMatchCasTrim() {
        internalTestOneExistingAuthorizerProfileDoesNotMatch("   NaME       ");
    }

    private void internalTestOneExistingAuthorizerProfileDoesNotMatch(final String name) {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        assertFalse(checker.isAuthorized(null, profiles, name, authorizers));
    }

    @Test(expected = TechnicalException.class)
    public void testOneAuthorizerDoesNotExist() {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        checker.isAuthorized(null, profiles, VALUE, authorizers);
    }

    @Test
    public void testTwoExistingAuthorizerProfileMatch() {
        profile.setId(VALUE);
        profile.addRole(ROLE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        authorizers.put(VALUE, new RequireAnyRoleAuthorizer(ROLE));
        assertTrue(checker.isAuthorized(null, profiles, NAME + Pac4jConstants.ELEMENT_SEPRATOR + VALUE, authorizers));
    }

    @Test
    public void testTwoExistingAuthorizerProfileDoesNotMatch() {
        profile.addRole(ROLE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        authorizers.put(VALUE, new RequireAnyRoleAuthorizer(ROLE));
        assertFalse(checker.isAuthorized(null, profiles, NAME + Pac4jConstants.ELEMENT_SEPRATOR + VALUE, authorizers));
    }

    @Test(expected = TechnicalException.class)
    public void testTwoAuthorizerOneDoesNotExist() {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        checker.isAuthorized(null, profiles, NAME + Pac4jConstants.ELEMENT_SEPRATOR + VALUE, authorizers);
    }

    @Test(expected = TechnicalException.class)
    public void testNullAuthorizers() {
        assertTrue(checker.isAuthorized(null, profiles, null));
        checker.isAuthorized(null, profiles, "auth1", null);
    }

    @Test
    public void testZeroAuthorizers() {
        assertTrue(checker.isAuthorized(null, profiles, new ArrayList<>()));
        assertTrue(checker.isAuthorized(null, profiles, "", new HashMap<>()));
    }

    @Test
    public void testOneExistingAuthorizerProfileMatch2() {
        profile.setId(VALUE);
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new IdAuthorizer());
        assertTrue(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test
    public void testOneExistingAuthorizerProfileDoesNotMatch2() {
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new IdAuthorizer());
        assertFalse(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test
    public void testTwoExistingAuthorizerProfileMatch2() {
        profile.setId(VALUE);
        profile.addRole(ROLE);
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new IdAuthorizer());
        authorizers.add(new RequireAnyRoleAuthorizer(ROLE));
        assertTrue(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test
    public void testTwoExistingAuthorizerProfileDoesNotMatch2() {
        profile.addRole(ROLE);
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new IdAuthorizer());
        authorizers.add(new RequireAnyRoleAuthorizer(ROLE));
        assertFalse(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test(expected = TechnicalException.class)
    public void testNullProfile() {
        checker.isAuthorized(null, null, new ArrayList<>());
    }

    @Test
    public void testHsts() {
        final MockWebContext context = MockWebContext.create();
        context.setScheme(SCHEME_HTTPS);
        checker.isAuthorized(context, profiles, DefaultAuthorizers.HSTS, null);
        assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
    }

    @Test
    public void testHstsCaseTrim() {
        final MockWebContext context = MockWebContext.create();
        context.setScheme(SCHEME_HTTPS);
        checker.isAuthorized(context, profiles, "  HSTS ", null);
        assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
    }

    @Test
    public void testNosniff() {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, DefaultAuthorizers.NOSNIFF, null);
        assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
    }

    @Test
    public void testNoframe() {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, DefaultAuthorizers.NOFRAME, null);
        assertNotNull(context.getResponseHeaders().get("X-Frame-Options"));
    }

    @Test
    public void testXssprotection() {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, DefaultAuthorizers.XSSPROTECTION, null);
        assertNotNull(context.getResponseHeaders().get("X-XSS-Protection"));
    }

    @Test
    public void testNocache() {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, DefaultAuthorizers.NOCACHE, null);
        assertNotNull(context.getResponseHeaders().get("Cache-Control"));
        assertNotNull(context.getResponseHeaders().get("Pragma"));
        assertNotNull(context.getResponseHeaders().get("Expires"));
    }

    @Test
    public void testAllowAjaxRequests() {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, DefaultAuthorizers.ALLOW_AJAX_REQUESTS, null);
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
        checker.isAuthorized(context, profiles, DefaultAuthorizers.SECURITYHEADERS, null);
        assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
        assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
        assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
        assertNotNull(context.getResponseHeaders().get("X-XSS-Protection"));
        assertNotNull(context.getResponseHeaders().get("Cache-Control"));
        assertNotNull(context.getResponseHeaders().get("Pragma"));
        assertNotNull(context.getResponseHeaders().get("Expires"));
    }

    @Test
    public void testCsrf() {
        final MockWebContext context = MockWebContext.create();
        assertTrue(checker.isAuthorized(context, profiles, DefaultAuthorizers.CSRF, null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfToken() {
        final MockWebContext context = MockWebContext.create();
        assertTrue(checker.isAuthorized(context, profiles, DefaultAuthorizers.CSRF_TOKEN, null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfPost() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HTTP_METHOD.POST.name());
        assertFalse(checker.isAuthorized(context, profiles, DefaultAuthorizers.CSRF, null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfTokenPost() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HTTP_METHOD.POST.name());
        assertTrue(checker.isAuthorized(context, profiles, DefaultAuthorizers.CSRF_TOKEN, null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfPostTokenParameter() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HTTP_METHOD.POST.name());
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        final String token = generator.get(context);
        context.addRequestParameter(Pac4jConstants.CSRF_TOKEN, token);
        assertTrue(checker.isAuthorized(context, profiles, DefaultAuthorizers.CSRF, null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfCheckPost() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HTTP_METHOD.POST.name());
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        generator.get(context);
        assertFalse(checker.isAuthorized(context, profiles, DefaultAuthorizers.CSRF_CHECK, null));
    }

    @Test
    public void testCsrfCheckPostTokenParameter() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HTTP_METHOD.POST.name());
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        final String token = generator.get(context);
        context.addRequestParameter(Pac4jConstants.CSRF_TOKEN, token);
        assertTrue(checker.isAuthorized(context, profiles, DefaultAuthorizers.CSRF_CHECK, null));
    }

    @Test
    public void testIsAnonymous() {
        profiles.clear();
        profiles.add(new AnonymousProfile());
        assertTrue(checker.isAuthorized(null, profiles, DefaultAuthorizers.IS_ANONYMOUS, null));
    }

    @Test
    public void testIsAuthenticated() {
        assertTrue(checker.isAuthorized(null, profiles, DefaultAuthorizers.IS_AUTHENTICATED, null));
    }

    @Test
    public void testIsFullyAuthenticated() {
        assertTrue(checker.isAuthorized(null, profiles, DefaultAuthorizers.IS_FULLY_AUTHENTICATED, null));
    }

    @Test
    public void testIsRemembered() {
        profile.setRemembered(true);
        assertTrue(checker.isAuthorized(null, profiles, DefaultAuthorizers.IS_REMEMBERED, null));
    }
}
