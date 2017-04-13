package org.pac4j.config.client;

import org.pac4j.config.builder.LdapAuthenticatorBuilder;
import org.pac4j.config.builder.DirectClientBuilder;
import org.pac4j.config.builder.IndirectClientBuilder;
import org.pac4j.config.builder.OAuthBuilder;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Build a configuration from properties.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class PropertiesConfigFactory implements ConfigFactory {

    public static final String AUTHENTICATOR_TEST_TOKEN = "testToken";
    public static final String AUTHENTICATOR_TEST_USERNAME_PASSWORD = "testUsernamePassword";

    private final String callbackUrl;

    private final OAuthBuilder oAuthBuilder;

    private final DirectClientBuilder directClientBuilder;

    private final IndirectClientBuilder indirectClientBuilder;

    private final LdapAuthenticatorBuilder ldapAuthenticatorBuilder;

    public PropertiesConfigFactory(final Map<String, String> properties) {
        this(null, properties);
    }

    public PropertiesConfigFactory(final String callbackUrl, final Map<String, String> properties) {
        this.callbackUrl = callbackUrl;
        this.oAuthBuilder = new OAuthBuilder(properties);
        directClientBuilder = new DirectClientBuilder(properties);
        indirectClientBuilder = new IndirectClientBuilder(properties);
        ldapAuthenticatorBuilder = new LdapAuthenticatorBuilder(properties);
    }

    public Config build(final Object... parameters) {
        final List<Client> clients = new ArrayList<>();
        oAuthBuilder.tryCreateFacebookClient(clients);
        oAuthBuilder.tryCreateTwitterClient(clients);
        indirectClientBuilder.tryCreateSaml2Client(clients);
        indirectClientBuilder.tryCreateCasClient(clients);
        indirectClientBuilder.tryCreateOidcClient(clients);
        oAuthBuilder.tryCreateDropboxClient(clients);
        oAuthBuilder.tryCreateGithubClient(clients);
        oAuthBuilder.tryCreateYahooClient(clients);
        oAuthBuilder.tryCreateGoogleClient(clients);
        oAuthBuilder.tryCreateFoursquareClient(clients);
        oAuthBuilder.tryCreateWindowsLiveClient(clients);
        oAuthBuilder.tryCreateLinkedInClient(clients);
        directClientBuilder.tryCreateAnonymousClient(clients);

        final Map<String, Authenticator> authenticators = new HashMap<>();
        authenticators.put(AUTHENTICATOR_TEST_TOKEN, new SimpleTestTokenAuthenticator());
        authenticators.put(AUTHENTICATOR_TEST_USERNAME_PASSWORD, new SimpleTestUsernamePasswordAuthenticator());

        ldapAuthenticatorBuilder.tryBuildLdapAuthenticator(authenticators);

        indirectClientBuilder.tryCreateLoginFormClient(clients, authenticators);
        indirectClientBuilder.tryCreateIndirectBasciAuthClient(clients, authenticators);

        return new Config(callbackUrl, clients);
    }
}
