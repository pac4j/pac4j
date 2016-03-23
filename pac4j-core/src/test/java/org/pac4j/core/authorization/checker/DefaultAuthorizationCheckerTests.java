package org.pac4j.core.authorization.checker;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.authorization.authorizer.csrf.DefaultCsrfTokenGenerator;
import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.TestsConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the {@link DefaultAuthorizationChecker}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultAuthorizationCheckerTests implements TestsConstants {

    private final AuthorizationChecker checker = new DefaultAuthorizationChecker();

    private List<UserProfile> profiles;

    private UserProfile profile;

    @Before
    public void setUp() {
        profile = new CommonProfile();
        profiles = new ArrayList<>();
        profiles.add(profile);
    }

    private static class IdAuthorizer implements Authorizer<UserProfile> {
        public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles) {
            return VALUE.equals(profiles.get(0).getId());
        }
    }

    @Test
    public void testBlankAuthorizerNameAProfile() throws RequiresHttpAction {
        assertTrue(checker.isAuthorized(null, profiles, null, null));
    }

    @Test
    public void testOneExistingAuthorizerProfileMatch() throws RequiresHttpAction {
        profile.setId(VALUE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        assertTrue(checker.isAuthorized(null, profiles, NAME, authorizers));
    }

    @Test
    public void testOneExistingAuthorizerProfileDoesNotMatch() throws RequiresHttpAction {
        internalTestOneExistingAuthorizerProfileDoesNotMatch(NAME);
    }

    @Test
    public void testOneExistingAuthorizerProfileDoesNotMatchCasTrim() throws RequiresHttpAction {
        internalTestOneExistingAuthorizerProfileDoesNotMatch("   NaME       ");
    }

    private void internalTestOneExistingAuthorizerProfileDoesNotMatch(final String name) throws RequiresHttpAction {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        assertFalse(checker.isAuthorized(null, profiles, name, authorizers));
    }

    @Test(expected = TechnicalException.class)
    public void testOneAuthorizerDoesNotExist() throws RequiresHttpAction {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        checker.isAuthorized(null, profiles, VALUE, authorizers);
    }

    @Test
    public void testTwoExistingAuthorizerProfileMatch() throws RequiresHttpAction {
        profile.setId(VALUE);
        profile.addRole(ROLE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        authorizers.put(VALUE, new RequireAnyRoleAuthorizer(ROLE));
        assertTrue(checker.isAuthorized(null, profiles, NAME + Pac4jConstants.ELEMENT_SEPRATOR + VALUE, authorizers));
    }

    @Test
    public void testTwoExistingAuthorizerProfileDoesNotMatch() throws RequiresHttpAction {
        profile.addRole(ROLE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        authorizers.put(VALUE, new RequireAnyRoleAuthorizer(ROLE));
        assertFalse(checker.isAuthorized(null, profiles, NAME + Pac4jConstants.ELEMENT_SEPRATOR + VALUE, authorizers));
    }

    @Test(expected = TechnicalException.class)
    public void testTwoAuthorizerOneDoesNotExist() throws RequiresHttpAction {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        checker.isAuthorized(null, profiles, NAME + Pac4jConstants.ELEMENT_SEPRATOR + VALUE, authorizers);
    }

    @Test(expected = TechnicalException.class)
    public void testNullAuthorizers() throws RequiresHttpAction {
        assertTrue(checker.isAuthorized(null, profiles, null));
        checker.isAuthorized(null, profiles, "auth1", null);
    }

    @Test
    public void testZeroAuthorizers() throws RequiresHttpAction {
        assertTrue(checker.isAuthorized(null, profiles, new ArrayList<>()));
        assertTrue(checker.isAuthorized(null, profiles, "", new HashMap<>()));
    }

    @Test
    public void testOneExistingAuthorizerProfileMatch2() throws RequiresHttpAction {
        profile.setId(VALUE);
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new IdAuthorizer());
        assertTrue(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test
    public void testOneExistingAuthorizerProfileDoesNotMatch2() throws RequiresHttpAction {
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new IdAuthorizer());
        assertFalse(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test
    public void testTwoExistingAuthorizerProfileMatch2() throws RequiresHttpAction {
        profile.setId(VALUE);
        profile.addRole(ROLE);
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new IdAuthorizer());
        authorizers.add(new RequireAnyRoleAuthorizer(ROLE));
        assertTrue(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test
    public void testTwoExistingAuthorizerProfileDoesNotMatch2() throws RequiresHttpAction {
        profile.addRole(ROLE);
        final List<Authorizer> authorizers = new ArrayList<>();
        authorizers.add(new IdAuthorizer());
        authorizers.add(new RequireAnyRoleAuthorizer(ROLE));
        assertFalse(checker.isAuthorized(null, profiles, authorizers));
    }

    @Test(expected = TechnicalException.class)
    public void testNullProfile() throws RequiresHttpAction {
        checker.isAuthorized(null, null, new ArrayList<Authorizer>());
    }

    @Test
    public void testHsts() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create();
        context.setScheme("HTTPS");
        checker.isAuthorized(context, profiles, "hsts", null);
        assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
    }

    @Test
    public void testHstsCaseTrim() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create();
        context.setScheme("HTTPS");
        checker.isAuthorized(context, profiles, "  HSTS ", null);
        assertNotNull(context.getResponseHeaders().get("Strict-Transport-Security"));
    }

    @Test
    public void testNosniff() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, "nosniff", null);
        assertNotNull(context.getResponseHeaders().get("X-Content-Type-Options"));
    }

    @Test
    public void testNoframe() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, "noframe", null);
        assertNotNull(context.getResponseHeaders().get("X-Frame-Options"));
    }

    @Test
    public void testXssprotection() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, "xssprotection", null);
        assertNotNull(context.getResponseHeaders().get("X-XSS-Protection"));
    }

    @Test
    public void testNocache() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create();
        checker.isAuthorized(context, profiles, "nocache", null);
        assertNotNull(context.getResponseHeaders().get("Cache-Control"));
        assertNotNull(context.getResponseHeaders().get("Pragma"));
        assertNotNull(context.getResponseHeaders().get("Expires"));
    }

    @Test
    public void testSecurityHeaders() throws RequiresHttpAction {
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
    public void testCsrf() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create();
        assertTrue(checker.isAuthorized(context, profiles, "csrf", null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfToken() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create();
        assertTrue(checker.isAuthorized(context, profiles, "csrfToken", null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfPost() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod("post");
        assertFalse(checker.isAuthorized(context, profiles, "csrf", null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfTokenPost() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod("post");
        assertTrue(checker.isAuthorized(context, profiles, "csrfToken", null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfPostTokenParameter() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod("post");
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        final String token = generator.get(context);
        context.addRequestParameter(Pac4jConstants.CSRF_TOKEN, token);
        assertTrue(checker.isAuthorized(context, profiles, "csrf", null));
        assertNotNull(context.getRequestAttribute(Pac4jConstants.CSRF_TOKEN));
        assertNotNull(ContextHelper.getCookie(context.getResponseCookies(), Pac4jConstants.CSRF_TOKEN));
    }

    @Test
    public void testCsrfCheckPost() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod("post");
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        generator.get(context);
        assertFalse(checker.isAuthorized(context, profiles, "csrfCheck", null));
    }

    @Test
    public void testCsrfCheckPostTokenParameter() throws RequiresHttpAction {
        final MockWebContext context = MockWebContext.create().setRequestMethod("post");
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        final String token = generator.get(context);
        context.addRequestParameter(Pac4jConstants.CSRF_TOKEN, token);
        assertTrue(checker.isAuthorized(context, profiles, "csrfCheck", null));
    }
}
