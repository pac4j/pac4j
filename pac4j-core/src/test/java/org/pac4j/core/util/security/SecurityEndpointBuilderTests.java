package org.pac4j.core.util.security;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.CheckHttpMethodAuthorizer;
import org.pac4j.core.authorization.authorizer.IsAnonymousAuthorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.MockDirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.matching.matcher.HttpMethodMatcher;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.matching.matcher.PathMatcher;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link SecurityEndpointBuilder}.
 *
 * @author Jerome LELEU
 * @since 5.5.0
 */
public final class SecurityEndpointBuilderTests implements TestsConstants {

    private static final String CLIENT1 = "client1";
    private static final String AUTHORIZER1 = "authorizer1";
    private static final String MATCHER1 = "matcher1";

    private MockSecurityEndpoint endpoint;

    private Config config;

    @Before
    public void setUp() {
        endpoint = new MockSecurityEndpoint();
        config = new Config();
    }

    @Test
    public void buildNoParameters() {
        SecurityEndpointBuilder.buildConfig(endpoint, config);

        assertEquals(config, endpoint.getConfig());
        assertNull(endpoint.getClients());
        assertNull(endpoint.getAuthorizers());
        assertNull(endpoint.getMatchers());
        assertNull(endpoint.getSecurityLogic());
        assertNull(endpoint.getHttpActionAdapter());
    }

    @Test
    public void buildSimpleStringConfig() {
        SecurityEndpointBuilder.buildConfig(endpoint, config, CLIENT1, AUTHORIZER1, MATCHER1);

        assertEquals(config, endpoint.getConfig());
        assertEquals(CLIENT1, endpoint.getClients());
        assertEquals(AUTHORIZER1, endpoint.getAuthorizers());
        assertEquals(MATCHER1, endpoint.getMatchers());
        assertNull(endpoint.getSecurityLogic());
        assertNull(endpoint.getHttpActionAdapter());
    }

    @Test
    public void buildSimpleStringNoConfig() {
        TestsHelper.expectException(() -> SecurityEndpointBuilder.buildConfig(endpoint, CLIENT1, AUTHORIZER1, MATCHER1),
            TechnicalException.class, "Cannot accept strings without a provided Config");
    }

    @Test
    public void buildSimpleStringConfigAtTheEnd() {
        SecurityEndpointBuilder.buildConfig(endpoint, CLIENT1, AUTHORIZER1, MATCHER1, config);

        assertEquals(config, endpoint.getConfig());
        assertEquals(CLIENT1, endpoint.getClients());
        assertEquals(AUTHORIZER1, endpoint.getAuthorizers());
        assertEquals(MATCHER1, endpoint.getMatchers());
        assertNull(endpoint.getSecurityLogic());
        assertNull(endpoint.getHttpActionAdapter());
    }

    @Test
    public void buildFailsTooManyStrings() {
        TestsHelper.expectException(() -> SecurityEndpointBuilder.buildConfig(endpoint, config,
                CLIENT1, AUTHORIZER1, MATCHER1, "toomanystrings"), TechnicalException.class, "Too many strings used in constructor");
    }

    @Test
    public void buildSimpleObjectConfig() {
        final Authorizer authorizer = new IsAnonymousAuthorizer();
        final Matcher matcher = new PathMatcher();

        SecurityEndpointBuilder.buildConfig(endpoint, config, new MockDirectClient(KEY), authorizer, matcher);

        assertEquals(config, endpoint.getConfig());
        assertEquals(KEY, endpoint.getClients());
        assertAuthorizer(config, endpoint.getAuthorizers(), authorizer, 1);
        assertMatcher(config, endpoint.getMatchers(), matcher, 1);
        assertNull(endpoint.getSecurityLogic());
        assertNull(endpoint.getHttpActionAdapter());
    }

    @Test
    public void buildList() {
        final List<Client> clients = Arrays.asList(new MockDirectClient(KEY), new MockDirectClient(VALUE));
        final Authorizer authorizer1 = new IsAnonymousAuthorizer();
        final Authorizer authorizer2 = new CheckHttpMethodAuthorizer();
        final List<Authorizer> authorizers = Arrays.asList(authorizer1, authorizer2);
        final Matcher matcher1 = new PathMatcher();
        final Matcher matcher2 = new HttpMethodMatcher();
        final List<Matcher> matchers = Arrays.asList(matcher1, matcher2);

        SecurityEndpointBuilder.buildConfig(endpoint, config, clients, authorizers, matchers);

        assertEquals(config, endpoint.getConfig());
        assertAuthorizersAndMatchers(authorizer1, authorizer2, matcher1, matcher2);
    }

    protected void assertAuthorizersAndMatchers(final Authorizer authorizer1, final Authorizer authorizer2,
                                  final Matcher matcher1, final Matcher matcher2) {
        assertEquals(KEY + "," + VALUE, endpoint.getClients());
        final String[] authorizerNames = endpoint.getAuthorizers().split(",");
        assertAuthorizer(config, authorizerNames[0], authorizer1, 2);
        assertAuthorizer(config, authorizerNames[1], authorizer2, 2);
        final String[] matcherNames = endpoint.getMatchers().split(",");
        assertMatcher(config, matcherNames[0], matcher1, 2);
        assertMatcher(config, matcherNames[1], matcher2, 2);
        assertNull(endpoint.getSecurityLogic());
        assertNull(endpoint.getHttpActionAdapter());
    }

    @Test
    public void buildArray() {
        final Client[] clients = new Client[] {new MockDirectClient(KEY), new MockDirectClient(VALUE)};
        final Authorizer authorizer1 = new IsAnonymousAuthorizer();
        final Authorizer authorizer2 = new CheckHttpMethodAuthorizer();
        final Authorizer[] authorizers = new Authorizer[] {authorizer1, authorizer2};
        final Matcher matcher1 = new PathMatcher();
        final Matcher matcher2 = new HttpMethodMatcher();
        final Matcher[] matchers = new Matcher[] {matcher1, matcher2};

        SecurityEndpointBuilder.buildConfig(endpoint, config, clients, authorizers, matchers);

        assertEquals(config, endpoint.getConfig());
        assertAuthorizersAndMatchers(authorizer1, authorizer2, matcher1, matcher2);
    }

    @Test
    public void buildMultiple() {
        final Authorizer authorizer1 = new IsAnonymousAuthorizer();
        final Authorizer authorizer2 = new CheckHttpMethodAuthorizer();
        final Matcher matcher1 = new PathMatcher();
        final Matcher matcher2 = new HttpMethodMatcher();

        SecurityEndpointBuilder.buildConfig(endpoint, config, new MockDirectClient(KEY), new MockDirectClient(VALUE),
            authorizer1, authorizer2, matcher1, matcher2);

        assertEquals(config, endpoint.getConfig());
        assertAuthorizersAndMatchers(authorizer1, authorizer2, matcher1, matcher2);
    }

    @Test
    public void buildMultipleDifferentOrder() {
        final Authorizer authorizer1 = new IsAnonymousAuthorizer();
        final Authorizer authorizer2 = new CheckHttpMethodAuthorizer();
        final Authorizer[] authorizers = new Authorizer[] {authorizer1, authorizer2};
        final Matcher matcher1 = new PathMatcher();
        final Matcher matcher2 = new HttpMethodMatcher();

        SecurityEndpointBuilder.buildConfig(endpoint, config, authorizer1, matcher1,
            new MockDirectClient(KEY), matcher2, new MockDirectClient(VALUE), authorizer2);

        assertEquals(config, endpoint.getConfig());
        assertAuthorizersAndMatchers(authorizer1, authorizer2, matcher1, matcher2);
    }

    @Test
    public void buildMultipleMixedTypesDifferentOrder() {
        final Client[] clients = new Client[] {new MockDirectClient(KEY)};
        final Authorizer authorizer1 = new IsAnonymousAuthorizer();
        final Authorizer authorizer2 = new CheckHttpMethodAuthorizer();
        final Authorizer[] authorizers = new Authorizer[] {authorizer1};
        final Matcher matcher1 = new PathMatcher();
        final Matcher matcher2 = new HttpMethodMatcher();
        final Set<Matcher> matchers = new HashSet<>();
        matchers.add(matcher1);

        SecurityEndpointBuilder.buildConfig(endpoint, config, clients, new MockDirectClient(VALUE), authorizers, authorizer2,
            matchers, matcher2);

        assertEquals(config, endpoint.getConfig());
        assertAuthorizersAndMatchers(authorizer1, authorizer2, matcher1, matcher2);
    }

    @Test
    public void buildUnsupportedType() {
        TestsHelper.expectException(() -> SecurityEndpointBuilder.buildConfig(endpoint, config, true),
            TechnicalException.class, "Unsupported parameter type: true");
    }

    @Test
    public void buildOtherTypes() {
        SecurityEndpointBuilder.buildConfig(endpoint, config, mock(HttpActionAdapter.class), mock(SecurityLogic.class));

        assertEquals(config, endpoint.getConfig());
        assertNull(endpoint.getClients());
        assertNull(endpoint.getAuthorizers());
        assertNull(endpoint.getMatchers());
        assertNotNull(endpoint.getSecurityLogic());
        assertNotNull(endpoint.getHttpActionAdapter());
    }

    @Test
    public void buildTwoConfig() {
        TestsHelper.expectException(() -> SecurityEndpointBuilder.buildConfig(endpoint, config, config),
            TechnicalException.class, "Only one Config can be used");
    }

    @Test
    public void buildAllTypesWithoutConfig() {
        final Client client = new MockDirectClient(KEY);
        final Authorizer authorizer = new CheckHttpMethodAuthorizer();
        final Matcher matcher = new HttpMethodMatcher();
        final SecurityLogic logic = mock(SecurityLogic.class);
        final HttpActionAdapter adapter = mock(HttpActionAdapter.class);

        SecurityEndpointBuilder.buildConfig(endpoint, client, authorizer, matcher, adapter, logic);

        final Config config = endpoint.getConfig();
        assertNotNull(endpoint.getConfig());
        assertEquals(client, config.getClients().findClient(KEY).orElse(null));
        assertAuthorizer(config, endpoint.getAuthorizers(), authorizer, 1);
        assertMatcher(config, endpoint.getMatchers(), matcher, 1);
        assertEquals(logic, endpoint.getSecurityLogic());
        assertEquals(adapter, endpoint.getHttpActionAdapter());
    }

    protected void assertAuthorizer(final Config config, final String name, final Authorizer authorizer, final int size) {
        assertTrue(name.startsWith("$int_authorizer"));
        assertEquals(authorizer, config.getAuthorizers().get(name));
        assertEquals(size, config.getAuthorizers().size());
    }

    protected void assertMatcher(final Config config, final String name, final Matcher matcher, final int size) {
        assertTrue(name.startsWith("$int_matcher"));
        assertEquals(matcher, config.getMatchers().get(name));
        assertEquals(size, config.getMatchers().size());
    }
}
