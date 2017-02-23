package org.pac4j.core.authorization.checker;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.authorization.authorizer.csrf.DefaultCsrfTokenGenerator;
import org.pac4j.core.context.*;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Tests the {@link DefaultAuthorizationChecker}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultAuthorizationCheckerTests implements TestsConstants {

    private final DefaultAuthorizationChecker checker = new DefaultAuthorizationChecker();

    private List<CommonProfile> profiles;

    private CommonProfile profile;

    @Before
    public void setUp() {
        profile = new CommonProfile();
        profiles = new ArrayList<>();
        profiles.add(profile);
    }

    private static class IdAuthorizer implements Authorizer<CommonProfile> {
        public boolean isAuthorized(final WebContext context, final List<CommonProfile> profiles) {
            return VALUE.equals(profiles.get(0).getId());
        }
    }

    @Test
    public void testBlankAuthorizerNameAProfile() throws HttpAction {
        assertTrue(checker.isAuthorized(null, profiles, null, null));
    }

    @Test
    public void testOneExistingAuthorizerProfileMatch() throws HttpAction {
        profile.setId(VALUE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        assertTrue(checker.isAuthorized(null, profiles, NAME, authorizers));
    }

    @Test
    public void testOneExistingAuthorizerProfileDoesNotMatch() throws HttpAction {
        internalTestOneExistingAuthorizerProfileDoesNotMatch(NAME);
    }

    @Test
    public void testOneExistingAuthorizerProfileDoesNotMatchCasTrim() throws HttpAction {
        internalTestOneExistingAuthorizerProfileDoesNotMatch("   NaME       ");
    }

    private void internalTestOneExistingAuthorizerProfileDoesNotMatch(final String name) throws HttpAction {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        assertFalse(checker.isAuthorized(null, profiles, name, authorizers));
    }

    @Test(expected = TechnicalException.class)
    public void testOneAuthorizerDoesNotExist() throws HttpAction {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        checker.isAuthorized(null, profiles, VALUE, authorizers);
    }

    @Test
    public void testTwoExistingAuthorizerProfileMatch() throws HttpAction {
        profile.setId(VALUE);
        profile.addRole(ROLE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        authorizers.put(VALUE, new RequireAnyRoleAuthorizer(ROLE));
        assertTrue(checker.isAuthorized(null, profiles, NAME + Pac4jConstants.ELEMENT_SEPRATOR + VALUE, authorizers));
    }

    @Test
    public void testTwoExistingAuthorizerProfileDoesNotMatch() throws HttpAction {
        profile.addRole(ROLE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        authorizers.put(VALUE, new RequireAnyRoleAuthorizer(ROLE));
        assertFalse(checker.isAuthorized(null, profiles, NAME + Pac4jConstants.ELEMENT_SEPRATOR + VALUE, authorizers));
    }

    @Test(expected = TechnicalException.class)
    public void testTwoAuthorizerOneDoesNotExist() throws HttpAction {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        checker.isAuthorized(null, profiles, NAME + Pac4jConstants.ELEMENT_SEPRATOR + VALUE, authorizers);
    }

    @Test(expected = TechnicalException.class)
    public void testNullAuthorizers() throws HttpAction {
        assertTrue(checker.isAuthorized(null, profiles, null));
        checker.isAuthorized(null, profiles, "auth1", null);
    }

    @Test
    public void testZeroAuthorizers() throws HttpAction {
        assertTrue(checker.isAuthorized(null, profiles, new ArrayList<>()));
        assertTrue(checker.isAuthorized(null, profiles, "", new HashMap<>()));
    }

    @Test
    public void testOneExistingAuthorizerProfileMatch2() throws HttpAction {
        profile.setId(VALUE);
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new IdAuthorizer());
        assertTrue(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test
    public void testOneExistingAuthorizerProfileDoesNotMatch2() throws HttpAction {
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new IdAuthorizer());
        assertFalse(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test
    public void testTwoExistingAuthorizerProfileMatch2() throws HttpAction {
        profile.setId(VALUE);
        profile.addRole(ROLE);
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new IdAuthorizer());
        authorizers.add(new RequireAnyRoleAuthorizer(ROLE));
        assertTrue(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test
    public void testTwoExistingAuthorizerProfileDoesNotMatch2() throws HttpAction {
        profile.addRole(ROLE);
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new IdAuthorizer());
        authorizers.add(new RequireAnyRoleAuthorizer(ROLE));
        assertFalse(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test(expected = TechnicalException.class)
    public void testNullProfile() throws HttpAction {
        checker.isAuthorized(null, null, new ArrayList<Authorizer>());
    }

    @Test
    public void testHsts() throws HttpAction {
        final MockWebContext context = MockWebContext.create();
        context.setScheme("HTTPS");
        checker.isAuthorized(context, profiles, "hsts", null);
        assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
    }

    @Test
    public void testHstsCaseTrim() throws HttpAction {
        final MockWebContext context = MockWebContext.create();
        context.setScheme("HTTPS");
        checker.isAuthorized(context, profiles, "  HSTS ", null);
        assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
    }

    @Test
    public void testNosniff() throws HttpAction {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, "nosniff", null);
        assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
    }

    @Test
    public void testNoframe() throws HttpAction {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, "noframe", null);
        assertNotNull(context.getResponseHeaders().get("X-Frame-Options"));
    }

    @Test
    public void testXssprotection() throws HttpAction {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, "xssprotection", null);
        assertNotNull(context.getResponseHeaders().get("X-XSS-Protection"));
    }

    @Test
    public void testNocache() throws HttpAction {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, "nocache", null);
        assertNotNull(context.getResponseHeaders().get("Cache-Control"));
        assertNotNull(context.getResponseHeaders().get("Pragma"));
        assertNotNull(context.getResponseHeaders().get("Expires"));
    }

    @Test
    public void testAllowAjaxRequests() throws HttpAction {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, "allowAjaxRequests", null);
        assertEquals("*", context.getResponseHeaders().get(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER));
        assertEquals("true", context.getResponseHeaders().get(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER));
        final String methods = context.getResponseHeaders().get(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS_HEADER);
        final List<String> methodArray = Arrays.asList(methods.split(",")).stream().map(String::trim).collect(Collectors.toList());
        assertTrue(methodArray.contains("POST"));
        assertTrue(methodArray.contains("PUT"));
        assertTrue(methodArray.contains("DELETE"));
        assertTrue(methodArray.contains("OPTIONS"));
        assertTrue(methodArray.contains("GET"));
    }

    @Test
    public void testSecurityHeaders() throws HttpAction {
        final MockWebContext context = MockWebContext.create();
        context.setScheme("HTTPS");
        checker.isAuthorized(context, profiles, "securityHeaders", null);
        assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
        assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
        assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
        assertNotNull(context.getResponseHeaders().get("X-XSS-Protection"));
        assertNotNull(context.getResponseHeaders().get("Cache-Control"));
        assertNotNull(context.getResponseHeaders().get("Pragma"));
        assertNotNull(context.getResponseHeaders().get("Expires"));
    }

    @Test
    public void testCsrf() throws HttpAction {
        final MockWebContext context = MockWebContext.create();
        assertTrue(checker.isAuthorized(context, profiles, "csrf", null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfToken() throws HttpAction {
        final MockWebContext context = MockWebContext.create();
        assertTrue(checker.isAuthorized(context, profiles, "csrfToken", null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfPost() throws HttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST);
        assertFalse(checker.isAuthorized(context, profiles, "csrf", null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfTokenPost() throws HttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST);
        assertTrue(checker.isAuthorized(context, profiles, "csrfToken", null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfPostTokenParameter() throws HttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST);
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        final String token = generator.get(context);
        context.addRequestParameter(Pac4jConstants.CSRF_TOKEN, token);
        assertTrue(checker.isAuthorized(context, profiles, "csrf", null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfCheckPost() throws HttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST);
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        generator.get(context);
        assertFalse(checker.isAuthorized(context, profiles, "csrfCheck", null));
    }

    @Test
    public void testCsrfCheckPostTokenParameter() throws HttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST);
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        final String token = generator.get(context);
        context.addRequestParameter(Pac4jConstants.CSRF_TOKEN, token);
        assertTrue(checker.isAuthorized(context, profiles, "csrfCheck", null));
    }

    @Test
    public void testIsAnonymous() throws HttpAction {
        profiles.clear();
        profiles.add(new AnonymousProfile());
        assertTrue(checker.isAuthorized(null, profiles, "isAnonymous", null));
    }

    @Test
    public void testIsAuthenticated() throws HttpAction {
        assertTrue(checker.isAuthorized(null, profiles, "isAuthenticated", null));
    }

    @Test
    public void testIsFullyAuthenticated() throws HttpAction {
        assertTrue(checker.isAuthorized(null, profiles, "isFullyAuthenticated", null));
    }

    @Test
    public void testIsRemembered() throws HttpAction {
        profile.setRemembered(true);
        assertTrue(checker.isAuthorized(null, profiles, "isRemembered", null));
    }
}
