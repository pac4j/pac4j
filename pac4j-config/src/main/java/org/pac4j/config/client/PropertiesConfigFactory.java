package org.pac4j.config.client;

import lombok.val;
import org.pac4j.config.builder.*;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.password.PasswordEncoder;

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
public class PropertiesConfigFactory extends AbstractBuilder implements ConfigFactory {

    private final String callbackUrl;

    public PropertiesConfigFactory(final Map<String, String> properties) {
        this(null, properties);
    }

    public PropertiesConfigFactory(final String callbackUrl, final Map<String, String> properties) {
        super(properties);
        this.callbackUrl = callbackUrl;
    }

    @Override
    public Config build(final Object... parameters) {

        final List<Client> clients = new ArrayList<>();
        final Map<String, Authenticator> authenticators = new HashMap<>();
        final Map<String, PasswordEncoder> encoders = new HashMap<>();

        // spring-security-crypto dependency required
        if (hasSpringEncoder()) {
            val springEncoderBuilder = new SpringEncoderBuilder(properties);
            springEncoderBuilder.tryCreatePasswordEncoder(encoders);
        }
        // shiro-core dependency required
        if (hasShiroEncoder()) {
            val shiroEncoderBuilder = new ShiroEncoderBuilder(properties);
            shiroEncoderBuilder.tryCreatePasswordEncoder(encoders);
        }
        // pac4j-ldap dependency required
        if (hasLdapAuthenticator()) {
            val ldapAuthenticatorBuilder = new LdapAuthenticatorBuilder(properties);
            ldapAuthenticatorBuilder.tryBuildLdapAuthenticator(authenticators);
        }
        // pac4j-sql dependency required
        if (hasDbAuthenticator()) {
            val dbAuthenticatorBuilder = new DbAuthenticatorBuilder(properties);
            dbAuthenticatorBuilder.tryBuildDbAuthenticator(authenticators, encoders);
        }
        // pac4j-oauth dependency required
        if (hasOAuthClients()) {
            val oAuthBuilder = new OAuthBuilder(properties);
            oAuthBuilder.tryCreateFacebookClient(clients);
            oAuthBuilder.tryCreateTwitterClient(clients);
            oAuthBuilder.tryCreateDropboxClient(clients);
            oAuthBuilder.tryCreateGithubClient(clients);
            oAuthBuilder.tryCreateYahooClient(clients);
            oAuthBuilder.tryCreateGoogleClient(clients);
            oAuthBuilder.tryCreateFoursquareClient(clients);
            oAuthBuilder.tryCreateWindowsLiveClient(clients);
            oAuthBuilder.tryCreateLinkedInClient(clients);
            oAuthBuilder.tryCreateGenericOAuth2Clients(clients);
        }
        // pac4j-saml dependency required
        if (hasSaml2Clients()) {
            val saml2ClientBuilder = new Saml2ClientBuilder(properties);
            saml2ClientBuilder.tryCreateSaml2Client(clients);
        }
        // pac4j-cas dependency required
        if (hasCasClients()) {
            val casClientBuilder = new CasClientBuilder(properties);
            casClientBuilder.tryCreateCasClient(clients);
        }
        // pac4j-oidc dependency required
        if (hasOidcClients()) {
            val oidcClientBuilder = new OidcClientBuilder(properties);
            oidcClientBuilder.tryCreateOidcClient(clients);
        }
        // pac4j-http dependency required
        if (hasHttpAuthenticatorsOrClients()) {
            val restAuthenticatorBuilder = new RestAuthenticatorBuilder(properties);
            restAuthenticatorBuilder.tryBuildRestAuthenticator(authenticators);

            val indirectHttpClientBuilder = new IndirectHttpClientBuilder(properties, authenticators);
            indirectHttpClientBuilder.tryCreateLoginFormClient(clients);
            indirectHttpClientBuilder.tryCreateIndirectBasicAuthClient(clients);
            val directClientBuilder = new DirectClientBuilder(properties, authenticators);
            directClientBuilder.tryCreateAnonymousClient(clients);
            directClientBuilder.tryCreateDirectBasciAuthClient(clients);
        }
        return new Config(callbackUrl, clients);
    }

    protected boolean hasShiroEncoder() {
        for (var i = 0; i <= MAX_NUM_ENCODERS; i++) {
            if (isNotBlank(getProperty(SHIRO_ENCODER, i)) || containsProperty(SHIRO_ENCODER_GENERATE_PUBLIC_SALT, i)
                || containsProperty(SHIRO_ENCODER_HASH_ALGORITHM_NAME, i) || containsProperty(SHIRO_ENCODER_HASH_ITERATIONS, i)
                || containsProperty(SHIRO_ENCODER_PRIVATE_SALT, i)) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasSpringEncoder() {
        for (var i = 0; i <= MAX_NUM_ENCODERS; i++) {
            val type = getProperty(SPRING_ENCODER_TYPE, i);
            if (isNotBlank(type)) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasLdapAuthenticator() {
        for (var i = 0; i <= MAX_NUM_AUTHENTICATORS; i++) {
            val type = getProperty(LDAP_TYPE, i);
            if (isNotBlank(type)) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasDbAuthenticator() {
        for (var i = 0; i <= MAX_NUM_AUTHENTICATORS; i++) {
            val className = getProperty(DB_DATASOURCE_CLASS_NAME, i);
            val jdbcUrl = getProperty(DB_JDBC_URL, i);
            if (isNotBlank(className) || isNotBlank(jdbcUrl)) {
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
        if (isNotBlank(getProperty(OAUTH2_ID)) && isNotBlank(getProperty(OAUTH2_SECRET)) &&
                isNotBlank(getProperty(OAUTH2_AUTH_URL)) && isNotBlank(getProperty(OAUTH2_TOKEN_URL))) {
            return true;
        }
        return false;
    }

    protected boolean hasSaml2Clients() {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
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
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            if (isNotBlank(getProperty(CAS_LOGIN_URL, i))) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasOidcClients() {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            if (isNotBlank(getProperty(OIDC_ID, i)) && isNotBlank(getProperty(OIDC_SECRET, i))) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasHttpAuthenticatorsOrClients() {
        if (isNotBlank(getProperty(ANONYMOUS))) {
            return true;
        }
        for (var i = 0; i <= MAX_NUM_AUTHENTICATORS; i++) {
            if (isNotBlank(getProperty(REST_URL, i))) {
                return true;
            }
        }
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            if (isNotBlank(getProperty(FORMCLIENT_LOGIN_URL, i)) && isNotBlank(getProperty(FORMCLIENT_AUTHENTICATOR, i))) {
                return true;
            }
            if (isNotBlank(getProperty(INDIRECTBASICAUTH_AUTHENTICATOR, i))) {
                return true;
            }
            if (isNotBlank(getProperty(DIRECTBASICAUTH_AUTHENTICATOR, i))) {
                return true;
            }
        }
        return false;
    }
}
