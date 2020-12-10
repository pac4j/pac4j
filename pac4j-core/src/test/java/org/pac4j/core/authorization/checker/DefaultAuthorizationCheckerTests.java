package org.pac4j.core.authorization.checker;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.MockDirectClient;
import org.pac4j.core.client.MockIndirectClient;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.context.*;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.matching.matcher.csrf.DefaultCsrfTokenGenerator;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.BasicUserProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.*;

import static org.junit.Assert.*;

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

    private static class IdAuthorizer implements Authorizer {
        @Override
        public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles) {
            return VALUE.equals(profiles.get(0).getId());
        }
    }

    @Test
    public void testBlankAuthorizerNameAProfile() {
        assertTrue(checker.isAuthorized(MockWebContext.create(), profiles, "", new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testNullAuthorizerNameAProfileGetRequest() {
        assertTrue(checker.isAuthorized(MockWebContext.create(), profiles, null, new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testNullAuthorizerNameAProfilePostRequestNoIndirectClient() {
        final MockWebContext context = MockWebContext.create().setRequestMethod("POST");
        assertTrue(checker.isAuthorized(context, profiles, null, new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testNullAuthorizerNameAProfilePostRequestIndirectClient() {
        final MockWebContext context = MockWebContext.create().setRequestMethod("POST");
        final List<Client> clients = new ArrayList<>();
        clients.add(new MockIndirectClient("test"));
        assertFalse(checker.isAuthorized(context, profiles, null, new HashMap<>(), clients));
    }

    @Test
    public void testBlankAuthorizerNameAProfilePostRequestNoIndirectClient() {
        final MockWebContext context = MockWebContext.create().setRequestMethod("POST");
        assertTrue(checker.isAuthorized(context, profiles, " ", new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testBlankAuthorizerNameAProfilePostRequestIndirectClient() {
        final MockWebContext context = MockWebContext.create().setRequestMethod("POST");
        final List<Client> clients = new ArrayList<>();
        clients.add(new MockIndirectClient("test"));
        assertFalse(checker.isAuthorized(context, profiles, " ", new HashMap<>(), clients));
    }

    @Test
    public void testNoneAuthorizerNameAProfilePostRequest() {
        final MockWebContext context = MockWebContext.create().setRequestMethod("POST");
        assertTrue(checker.isAuthorized(context, profiles, "noNe", new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testOneExistingAuthorizerProfileMatch() {
        profile.setId(VALUE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        assertTrue(checker.isAuthorized(null, profiles, NAME, authorizers, new ArrayList<>()));
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
        assertFalse(checker.isAuthorized(null, profiles, name, authorizers, new ArrayList<>()));
    }

    @Test
    public void testOneAuthorizerDoesNotExist() {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        TestsHelper.expectException(() -> checker.isAuthorized(null, profiles, VALUE, authorizers, new ArrayList<>()),
            TechnicalException.class, "The authorizer '" + VALUE + "' must be defined in the security configuration");
    }

    @Test
    public void testTwoExistingAuthorizerProfileMatch() {
        profile.setId(VALUE);
        profile.addRole(ROLE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        authorizers.put(VALUE, new RequireAnyRoleAuthorizer(ROLE));
        assertTrue(checker.isAuthorized(null, profiles, NAME + Pac4jConstants.ELEMENT_SEPARATOR + VALUE, authorizers, new ArrayList<>()));
    }

    @Test
    public void testTwoExistingAuthorizerProfileDoesNotMatch() {
        profile.addRole(ROLE);
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        authorizers.put(VALUE, new RequireAnyRoleAuthorizer(ROLE));
        assertFalse(checker.isAuthorized(null, profiles, NAME + Pac4jConstants.ELEMENT_SEPARATOR + VALUE, authorizers, new ArrayList<>()));
    }

    @Test(expected = TechnicalException.class)
    public void testTwoAuthorizerOneDoesNotExist() {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(NAME, new IdAuthorizer());
        checker.isAuthorized(null, profiles, NAME + Pac4jConstants.ELEMENT_SEPARATOR + VALUE, authorizers, new ArrayList<>());
    }

    @Test(expected = TechnicalException.class)
    public void testNullAuthorizers() {
        assertTrue(checker.isAuthorized(null, profiles, null));
        checker.isAuthorized(null, profiles, "auth1", null, new ArrayList<>());
    }

    @Test
    public void testZeroAuthorizers() {
        assertTrue(checker.isAuthorized(MockWebContext.create(), profiles, new ArrayList<>()));
        assertTrue(checker.isAuthorized(MockWebContext.create(), profiles, "", new HashMap<>(), new ArrayList<>()));
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
    public void testCsrfCheckPost() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name());
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        generator.get(context);
        assertFalse(checker.isAuthorized(context, profiles, DefaultAuthorizers.CSRF_CHECK, new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testCsrfCheckPostTokenParameter() {
        final MockWebContext context = MockWebContext.create().setRequestMethod(HttpConstants.HTTP_METHOD.POST.name());
        final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();
        final String token = generator.get(context);
        context.addRequestParameter(Pac4jConstants.CSRF_TOKEN, token);
        assertTrue(checker.isAuthorized(context, profiles, DefaultAuthorizers.CSRF_CHECK, new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testIsAnonymous() {
        profiles.clear();
        profiles.add(new AnonymousProfile());
        assertTrue(checker.isAuthorized(null, profiles, DefaultAuthorizers.IS_ANONYMOUS, new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testIsAuthenticated() {
        assertTrue(checker.isAuthorized(null, profiles, DefaultAuthorizers.IS_AUTHENTICATED, new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testIsFullyAuthenticated() {
        assertTrue(checker.isAuthorized(null, profiles, DefaultAuthorizers.IS_FULLY_AUTHENTICATED, new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testIsRemembered() {
        profile.setRemembered(true);
        assertTrue(checker.isAuthorized(null, profiles, DefaultAuthorizers.IS_REMEMBERED, new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testDefaultAuthorizersNoClient() {
        assertEquals(Arrays.asList(DefaultAuthorizationChecker.IS_AUTHENTICATED_AUTHORIZER),
            checker.computeDefaultAuthorizers(MockWebContext.create(), new ArrayList<>(), new ArrayList<>(), new HashMap<>()));
    }

    @Test
    public void testDefaultAuthorizersAnonymousClient() {
        final List<Client> clients = Arrays.asList(AnonymousClient.INSTANCE);
        assertEquals(0, checker.computeDefaultAuthorizers(MockWebContext.create(), new ArrayList<>(), clients, new HashMap<>()).size());
    }

    @Test
    public void testDefaultAuthorizersDirectClient() {
        final List<Client> clients = Arrays.asList(new MockDirectClient("test"));
        assertEquals(Arrays.asList(DefaultAuthorizationChecker.IS_AUTHENTICATED_AUTHORIZER),
            checker.computeDefaultAuthorizers(MockWebContext.create(), new ArrayList<>(), clients, new HashMap<>()));
    }

    @Test
    public void testDefaultAuthorizersIndirectClient() {
        final List<Client> clients = Arrays.asList(new MockIndirectClient("test"));
        assertEquals(Arrays.asList(DefaultAuthorizationChecker.CSRF_AUTHORIZER, DefaultAuthorizationChecker.IS_AUTHENTICATED_AUTHORIZER),
            checker.computeDefaultAuthorizers(MockWebContext.create(), new ArrayList<>(), clients, new HashMap<>()));
    }

    @Test
    public void testDefaultAuthorizersIndirectAndAnonymousClients() {
        final List<Client> clients = Arrays.asList(new MockIndirectClient("test"), AnonymousClient.INSTANCE);
        assertEquals(Arrays.asList(DefaultAuthorizationChecker.CSRF_AUTHORIZER),
            checker.computeDefaultAuthorizers(MockWebContext.create(), new ArrayList<>(), clients, new HashMap<>()));
    }

    @Test
    public void testComputeAuthorizerNoClientIsFullyAuthenticated() {
        assertEquals(Arrays.asList(DefaultAuthorizationChecker.IS_FULLY_AUTHENTICATED_AUTHORIZER),
            checker.computeAuthorizers(MockWebContext.create(), new ArrayList<>(),
                DefaultAuthorizers.IS_FULLY_AUTHENTICATED, new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testComputeAuthorizerNoClientPlusIsFullyAuthenticated() {
        assertEquals(Arrays.asList(DefaultAuthorizationChecker.IS_AUTHENTICATED_AUTHORIZER,
            DefaultAuthorizationChecker.IS_FULLY_AUTHENTICATED_AUTHORIZER),
            checker.computeAuthorizers(MockWebContext.create(), new ArrayList<>(),
                "+" + DefaultAuthorizers.IS_FULLY_AUTHENTICATED, new HashMap<>(), new ArrayList<>()));
    }

    @Test
    public void testComputeAuthorizersOverrideDefault() {
        final Map<String, Authorizer> authorizers = new HashMap<>();
        authorizers.put(DefaultAuthorizers.IS_AUTHENTICATED, DefaultAuthorizationChecker.IS_FULLY_AUTHENTICATED_AUTHORIZER);
        assertEquals(Arrays.asList(DefaultAuthorizationChecker.IS_FULLY_AUTHENTICATED_AUTHORIZER),
            checker.computeAuthorizers(MockWebContext.create(), new ArrayList<>(), DefaultAuthorizers.IS_AUTHENTICATED,
                authorizers, new ArrayList<>()));
    }
}
