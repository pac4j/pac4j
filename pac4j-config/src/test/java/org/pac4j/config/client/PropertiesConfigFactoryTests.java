package org.pac4j.config.client;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.http.client.direct.DirectBasicAuthClient;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.http.client.indirect.IndirectBasicAuthClient;
import org.pac4j.http.credentials.authenticator.RestAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.ldap.profile.service.LdapProfileService;
import org.pac4j.ldap.test.tools.LdapServer;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.oidc.client.GoogleOidcClient;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.sql.profile.service.DbProfileService;
import org.pac4j.sql.test.tools.DbServer;

import java.util.HashMap;
import java.util.Map;

import static org.pac4j.config.builder.OAuthBuilder.*;
import static org.junit.Assert.*;
import static org.pac4j.ldap.test.tools.LdapServer.*;

/**
 * Tests {@link PropertiesConfigFactory}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class PropertiesConfigFactoryTests implements TestsConstants {

    @Test
    public void test() {
        LdapServer ldapServer = null;
        try {
            ldapServer = new LdapServer();
            ldapServer.start();
            new DbServer();

            final Map<String, String> properties = new HashMap<>();
            properties.put(FACEBOOK_ID, ID);
            properties.put(FACEBOOK_SECRET, SECRET);
            properties.put(TWITTER_ID, ID);
            properties.put(TWITTER_SECRET, SECRET);
            properties.put(CAS_LOGIN_URL, CALLBACK_URL);
            properties.put(CAS_PROTOCOL, CasProtocol.CAS20.toString());
            properties.put(SAML_KEYSTORE_PASSWORD, PASSWORD);
            properties.put(SAML_PRIVATE_KEY_PASSWORD, PASSWORD);
            properties.put(SAML_KEYSTORE_PATH, PATH);
            properties.put(SAML_IDENTITY_PROVIDER_METADATA_PATH, PATH);
            properties.put(SAML_DESTINATION_BINDING_TYPE, SAMLConstants.SAML2_REDIRECT_BINDING_URI);
            properties.put(SAML_KEYSTORE_ALIAS, VALUE);
            properties.put(OIDC_ID, ID);
            properties.put(OIDC_SECRET, SECRET);
            properties.put(OIDC_DISCOVERY_URI, CALLBACK_URL);
            properties.put(OIDC_USE_NONCE, "true");
            properties.put(OIDC_PREFERRED_JWS_ALGORITHM, "RS384");
            properties.put(OIDC_MAX_CLOCK_SKEW, "60");
            properties.put(OIDC_CLIENT_AUTHENTICATION_METHOD, "CLIENT_SECRET_POST");
            properties.put(OIDC_CUSTOM_PARAM_KEY + "1", KEY);
            properties.put(OIDC_CUSTOM_PARAM_VALUE + "1", VALUE);

            properties.put(CAS_LOGIN_URL.concat(".1"), LOGIN_URL);
            properties.put(CAS_PROTOCOL.concat(".1"), CasProtocol.CAS30.toString());

            properties.put(OIDC_TYPE.concat(".1"), "google");
            properties.put(OIDC_ID.concat(".1"), ID);
            properties.put(OIDC_SECRET.concat(".1"), SECRET);

            properties.put(ANONYMOUS, "whatever the value");

            properties.put(FORMCLIENT_LOGIN_URL, LOGIN_URL);
            properties.put(FORMCLIENT_AUTHENTICATOR, "testUsernamePassword");

            properties.put(INDIRECTBASICAUTH_AUTHENTICATOR.concat(".2"), "testUsernamePassword");

            properties.put(LDAP_TYPE, "direct");
            properties.put(LDAP_URL, "ldap://localhost:" + ldapServer.getPort());
            properties.put(LDAP_USE_SSL, "false");
            properties.put(LDAP_USE_START_TLS, "false");
            properties.put(LDAP_DN_FORMAT, CN + "=%s," + BASE_PEOPLE_DN);
            properties.put(LDAP_USERS_DN, BASE_PEOPLE_DN);
            properties.put(LDAP_PRINCIPAL_ATTRIBUTE_ID, CN);
            properties.put(LDAP_ATTRIBUTES, SN + "," + ROLE);

            properties.put(FORMCLIENT_LOGIN_URL.concat(".2"), PAC4J_BASE_URL);
            properties.put(FORMCLIENT_AUTHENTICATOR.concat(".2"), "ldap");

            properties.put(SPRING_ENCODER_TYPE.concat(".4"), "standard");
            properties.put(SPRING_ENCODER_STANDARD_SECRET.concat(".4"), SALT);

            properties.put(DB_JDBC_URL, "jdbc:h2:mem:test");
            properties.put(DB_USERNAME, Pac4jConstants.USERNAME);
            properties.put(DB_PASSWORD, Pac4jConstants.PASSWORD);
            properties.put(DB_USERNAME_ATTRIBUTE, Pac4jConstants.USERNAME);
            properties.put(DB_USER_PASSWORD_ATTRIBUTE, Pac4jConstants.PASSWORD);
            properties.put(DB_ATTRIBUTES, FIRSTNAME);
            properties.put(DB_PASSWORD_ENCODER, "encoder.spring.4");

            properties.put(INDIRECTBASICAUTH_AUTHENTICATOR.concat(".5"), "db");

            properties.put(REST_URL.concat(".3"), PAC4J_BASE_URL);
            properties.put(DIRECTBASICAUTH_AUTHENTICATOR.concat(".7"), "rest.3");

            final PropertiesConfigFactory factory = new PropertiesConfigFactory(CALLBACK_URL, properties);
            final Config config = factory.build();
            final Clients clients = config.getClients();
            assertEquals(13, clients.getClients().size());

            final FacebookClient fbClient = (FacebookClient) clients.findClient("FacebookClient");
            assertEquals(ID, fbClient.getKey());
            assertEquals(SECRET, fbClient.getSecret());

            assertNotNull(clients.findClient("AnonymousClient"));

            final TwitterClient twClient = (TwitterClient) clients.findClient("TwitterClient");
            assertEquals(ID, twClient.getKey());
            assertEquals(SECRET, twClient.getSecret());

            final CasClient casClient = (CasClient) clients.findClient("CasClient");
            assertEquals(CALLBACK_URL, casClient.getConfiguration().getLoginUrl());
            assertEquals(CasProtocol.CAS20, casClient.getConfiguration().getProtocol());

            final SAML2Client saml2client = (SAML2Client) clients.findClient("SAML2Client");
            assertNotNull(saml2client);
            final SAML2Configuration saml2Config = saml2client.getConfiguration();
            assertEquals(SAMLConstants.SAML2_REDIRECT_BINDING_URI, saml2Config.getDestinationBindingType());
            assertEquals(VALUE, saml2Config.getKeyStoreAlias());

            final OidcClient oidcClient = (OidcClient) clients.findClient("OidcClient");
            assertNotNull(oidcClient);
            assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_POST.toString(),
                oidcClient.getConfiguration().getClientAuthenticationMethod().toString().toLowerCase());

            final CasClient casClient1 = (CasClient) clients.findClient("CasClient.1");
            assertEquals(CasProtocol.CAS30, casClient1.getConfiguration().getProtocol());

            final GoogleOidcClient googleOidcClient = (GoogleOidcClient) clients.findClient("GoogleOidcClient.1");
            googleOidcClient.init();
            assertEquals(ID, googleOidcClient.getConfiguration().getClientId());
            assertEquals(SECRET, googleOidcClient.getConfiguration().getSecret());
            assertEquals("https://accounts.google.com/.well-known/openid-configuration",
                googleOidcClient.getConfiguration().getDiscoveryURI());
            assertEquals(CALLBACK_URL + "?client_name=GoogleOidcClient.1", googleOidcClient.getCallbackUrlResolver()
                .compute(googleOidcClient.getUrlResolver(), googleOidcClient.getCallbackUrl(),
                    googleOidcClient.getName(), MockWebContext.create()));

            final FormClient formClient = (FormClient) clients.findClient("FormClient");
            assertEquals(LOGIN_URL, formClient.getLoginUrl());
            assertTrue(formClient.getAuthenticator() instanceof SimpleTestUsernamePasswordAuthenticator);

            final FormClient formClient2 = (FormClient) clients.findClient("FormClient.2");
            assertEquals(PAC4J_BASE_URL, formClient2.getLoginUrl());
            assertTrue(formClient2.getAuthenticator() instanceof LdapProfileService);
            final LdapProfileService ldapAuthenticator = (LdapProfileService) formClient2.getAuthenticator();
            final UsernamePasswordCredentials ldapCredentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
            ldapAuthenticator.validate(ldapCredentials, MockWebContext.create());
            assertNotNull(ldapCredentials.getUserProfile());

            final IndirectBasicAuthClient indirectBasicAuthClient =
                (IndirectBasicAuthClient) clients.findClient("IndirectBasicAuthClient.2");
            assertEquals("authentication required", indirectBasicAuthClient.getRealmName());
            assertTrue(indirectBasicAuthClient.getAuthenticator() instanceof SimpleTestUsernamePasswordAuthenticator);

            final IndirectBasicAuthClient indirectBasicAuthClient2 =
                (IndirectBasicAuthClient) clients.findClient("IndirectBasicAuthClient.5");
            assertTrue(indirectBasicAuthClient2.getAuthenticator() instanceof DbProfileService);
            final DbProfileService dbAuthenticator = (DbProfileService) indirectBasicAuthClient2.getAuthenticator();
            assertNotNull(dbAuthenticator);
            final UsernamePasswordCredentials dbCredentials = new UsernamePasswordCredentials(GOOD_USERNAME, PASSWORD);
            dbAuthenticator.validate(dbCredentials, MockWebContext.create());
            assertNotNull(dbCredentials.getUserProfile());

            final DirectBasicAuthClient directBasicAuthClient = (DirectBasicAuthClient) clients.findClient("DirectBasicAuthClient.7");
            assertNotNull(directBasicAuthClient);
            final RestAuthenticator restAuthenticator = (RestAuthenticator) directBasicAuthClient.getAuthenticator();
            assertEquals(PAC4J_BASE_URL, restAuthenticator.getUrl());

        } finally {
            if (ldapServer != null) {
                ldapServer.stop();
            }
        }
    }
}
