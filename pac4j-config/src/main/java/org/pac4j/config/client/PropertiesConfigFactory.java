package org.pac4j.config.client;

import org.pac4j.config.builder.*;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.credentials.authenticator.Authenticator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Build a configuration from properties.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class PropertiesConfigFactory extends AbstractBuilder implements ConfigFactory, PropertiesConstants {

    private final String callbackUrl;

    public PropertiesConfigFactory(final Map<String, String> properties) {
        this(null, properties);
    }

    public PropertiesConfigFactory(final String callbackUrl, final Map<String, String> properties) {
        super(properties);
        this.callbackUrl = callbackUrl;
    }

    public Config build(final Object... parameters) {

        final List<Client> clients = new ArrayList<>();
        final Map<String, Authenticator> authenticators = new HashMap<>();

        // pac4j-ldap dependency required
        if (hasLdapAuthenticator()) {
            final LdapAuthenticatorBuilder ldapAuthenticatorBuilder = new LdapAuthenticatorBuilder(properties);
            ldapAuthenticatorBuilder.tryBuildLdapAuthenticator(authenticators);
        }
        // pac4j-oauth dependency required
        if (hasOAuthClients()) {
            final OAuthBuilder oAuthBuilder = new OAuthBuilder(properties);
            oAuthBuilder.tryCreateFacebookClient(clients);
            oAuthBuilder.tryCreateTwitterClient(clients);
            oAuthBuilder.tryCreateDropboxClient(clients);
            oAuthBuilder.tryCreateGithubClient(clients);
            oAuthBuilder.tryCreateYahooClient(clients);
            oAuthBuilder.tryCreateGoogleClient(clients);
            oAuthBuilder.tryCreateFoursquareClient(clients);
            oAuthBuilder.tryCreateWindowsLiveClient(clients);
            oAuthBuilder.tryCreateLinkedInClient(clients);
        }
        // pac4j-saml dependency required
        if (hasSaml2Clients()) {
            final Saml2ClientBuilder saml2ClientBuilder = new Saml2ClientBuilder(properties);
            saml2ClientBuilder.tryCreateSaml2Client(clients);
        }
        // pac4j-cas dependency required
        if (hasCasClients()) {
            final CasClientBuilder casClientBuilder = new CasClientBuilder(properties);
            casClientBuilder.tryCreateCasClient(clients);
        }
        // pac4j-oidc dependency required
        if (hasOidcClients()) {
            final OidcClientBuilder oidcClientBuilder = new OidcClientBuilder(properties);
            oidcClientBuilder.tryCreateOidcClient(clients);
        }
        // pac4j-http dependency required
        if (hasHttpClients()) {
            final IndirectHttpClientBuilder indirectHttpClientBuilder = new IndirectHttpClientBuilder(properties, authenticators);
            indirectHttpClientBuilder.tryCreateLoginFormClient(clients);
            indirectHttpClientBuilder.tryCreateIndirectBasciAuthClient(clients);
            final DirectClientBuilder directClientBuilder = new DirectClientBuilder(properties);
            directClientBuilder.tryCreateAnonymousClient(clients);
        }

        return new Config(callbackUrl, clients);
    }

    protected boolean hasLdapAuthenticator() {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final String type = getProperty(LDAP_TYPE, i);
            if (isNotBlank(type)) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasOAuthClients() {
        if (isNotBlank(getProperty(LINKEDIN_ID)) && isNotBlank(getProperty(LINKEDIN_SECRET))) {
            return true;
        }
        if (isNotBlank(getProperty(FACEBOOK_ID)) && isNotBlank(getProperty(FACEBOOK_SECRET))) {
            return true;
        }
        if (isNotBlank(getProperty(WINDOWSLIVE_ID)) && isNotBlank(getProperty(WINDOWSLIVE_SECRET))) {
            return true;
        }
        if (isNotBlank(getProperty(FOURSQUARE_ID)) && isNotBlank(getProperty(FOURSQUARE_SECRET))) {
            return true;
        }
        if (isNotBlank(getProperty(GOOGLE_ID)) && isNotBlank(getProperty(GOOGLE_SECRET))) {
            return true;
        }
        if (isNotBlank(getProperty(YAHOO_ID)) && isNotBlank(getProperty(YAHOO_SECRET))) {
            return true;
        }
        if (isNotBlank(getProperty(DROPBOX_ID)) && isNotBlank(getProperty(DROPBOX_SECRET))) {
            return true;
        }
        if (isNotBlank(getProperty(GITHUB_ID)) && isNotBlank(getProperty(GITHUB_SECRET))) {
            return true;
        }
        if (isNotBlank(getProperty(TWITTER_ID)) && isNotBlank(getProperty(TWITTER_SECRET))) {
            return true;
        }
        return false;
    }

    protected boolean hasSaml2Clients() {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            if (isNotBlank(getProperty(SAML_KEYSTORE_PASSWORD, i)) &&
                    isNotBlank(getProperty(SAML_PRIVATE_KEY_PASSWORD, i)) &&
                    isNotBlank(getProperty(SAML_KEYSTORE_PATH, i)) &&
                    isNotBlank(getProperty(SAML_IDENTITY_PROVIDER_METADATA_PATH, i))) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasCasClients() {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            if (isNotBlank(getProperty(CAS_LOGIN_URL, i))) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasOidcClients() {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            if (isNotBlank(getProperty(OIDC_ID, i)) && isNotBlank(getProperty(OIDC_SECRET, i))) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasHttpClients() {
        if (isNotBlank(getProperty(ANONYMOUS))) {
            return true;
        }
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            if (isNotBlank(getProperty(FORMCLIENT_LOGIN_URL, i)) && isNotBlank(getProperty(FORMCLIENT_AUTHENTICATOR, i))) {
                return true;
            }
            if (isNotBlank(getProperty(INDIRECTBASICAUTH_AUTHENTICATOR, i))) {
                return true;
            }
        }
        return false;
    }
}
