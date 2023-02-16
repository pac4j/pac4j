package org.pac4j.oidc.credentials.authenticator;

import static org.junit.Assert.assertEquals;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;

/**
 * @author Mathias Loesch
 * @since 5.7.1
 */
public class OidcAuthenticatorTest {


    @Test
    public void shouldUseFirstServerSupportedAuthMethod() throws URISyntaxException {
        OidcConfiguration configuration = new OidcConfiguration();
        configuration.setClientId("clientId");
        configuration.setSecret("secret");

        OIDCProviderMetadata providerMetadata = getOidcProviderMetadata(
            List.of(ClientAuthenticationMethod.CLIENT_SECRET_POST, ClientAuthenticationMethod.CLIENT_SECRET_BASIC));
        configuration.setProviderMetadata(providerMetadata);

        OidcAuthenticator oidcAuthenticator = new OidcAuthenticator(configuration, new OidcClient(configuration));

        assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_POST, oidcAuthenticator.getClientAuthentication().getMethod());
    }

    @Test
    public void shouldRespectClientSupportedAuthMethod() throws URISyntaxException {
        OidcConfiguration configuration = new OidcConfiguration();
        configuration.setClientId("clientId");
        configuration.setSecret("secret");
        configuration.setSupportedClientAuthenticationMethods(Set.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC));

        OIDCProviderMetadata providerMetadata = getOidcProviderMetadata(
            List.of(ClientAuthenticationMethod.PRIVATE_KEY_JWT, ClientAuthenticationMethod.CLIENT_SECRET_BASIC));
        configuration.setProviderMetadata(providerMetadata);

        OidcAuthenticator oidcAuthenticator = new OidcAuthenticator(configuration, new OidcClient(configuration));

        assertEquals(ClientAuthenticationMethod.CLIENT_SECRET_BASIC, oidcAuthenticator.getClientAuthentication().getMethod());
    }

    @Test
    public void shouldFailInCaseOfNoCommonAuthMethod() throws URISyntaxException {
        OidcConfiguration configuration = new OidcConfiguration();
        configuration.setClientId("clientId");
        configuration.setSecret("secret");
        configuration.setSupportedClientAuthenticationMethods(Set.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC));

        OIDCProviderMetadata providerMetadata = getOidcProviderMetadata(List.of(ClientAuthenticationMethod.CLIENT_SECRET_POST));
        configuration.setProviderMetadata(providerMetadata);

        try {
            new OidcAuthenticator(configuration, new OidcClient(configuration));
            Assert.fail("TechnicalException expected");
        } catch (TechnicalException e) {
            assertEquals("None of the Token endpoint provider metadata authentication methods are supported: [client_secret_post]",
                e.getMessage());
        }

    }


    private static OIDCProviderMetadata getOidcProviderMetadata(List<ClientAuthenticationMethod> supportedClientAuthenticationMethods)
        throws URISyntaxException {
        OIDCProviderMetadata providerMetadata = new OIDCProviderMetadata(new Issuer("issuer"), List.of(SubjectType.PUBLIC), new URI(""));
        providerMetadata.setTokenEndpointAuthMethods(supportedClientAuthenticationMethods);
        return providerMetadata;
    }
}
