package org.pac4j.oidc.metadata;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import org.pac4j.oidc.config.AzureAd2OidcConfiguration;
import org.pac4j.oidc.config.OidcConfiguration;

/**
 * @author Mathias Loesch
 * @since 6.0.4
 */
public class OidcOpMetadataResolverTestBase {

    public static final JWSAlgorithm JWS_ALGORITHM = JWSAlgorithm.HS256;

    protected static OidcConfiguration getOidcConfiguration(Set<ClientAuthenticationMethod> supportedClientAuthenticationMethods,
        String discoveryURI) {
        OidcConfiguration configuration = new OidcConfiguration();
        configuration.setClientId("clientId");
        configuration.setSecret("secret");
        configuration.setDiscoveryURI(discoveryURI);
        configuration.setPreferredJwsAlgorithm(OidcOpMetadataResolverTestBase.JWS_ALGORITHM);
        configuration.setSupportedClientAuthenticationMethods(supportedClientAuthenticationMethods);
        return configuration;
    }

    protected static StaticOidcOpMetadataResolver getStaticMetadataResolver(OidcConfiguration configuration,
        List<ClientAuthenticationMethod> supportedAuthMethods) throws URISyntaxException {
        OIDCProviderMetadata providerMetadata = OidcOpMetadataResolverTestBase.getOidcProviderMetadata(supportedAuthMethods);
        StaticOidcOpMetadataResolver oidcOpMetadataResolver = new StaticOidcOpMetadataResolver(configuration, providerMetadata);

        oidcOpMetadataResolver.init();
        return oidcOpMetadataResolver;
    }
    
    protected static AzureAd2OidcConfiguration getAzureAd2OidcConfiguration(Set<ClientAuthenticationMethod> supportedClientAuthenticationMethods) {
        AzureAd2OidcConfiguration configuration = new AzureAd2OidcConfiguration();
        configuration.setClientId("clientId");
        configuration.setSecret("secret");
        configuration.setPreferredJwsAlgorithm(OidcOpMetadataResolverTestBase.JWS_ALGORITHM);
        configuration.setSupportedClientAuthenticationMethods(supportedClientAuthenticationMethods);
        return configuration;
    }
    
    protected static AzureAdOpMetadataResolver getAzureAdOpMetadataResolver(OidcConfiguration configuration,
            List<ClientAuthenticationMethod> supportedAuthMethods) throws URISyntaxException {
        AzureAdOpMetadataResolver oidcOpMetadataResolver = new AzureAdOpMetadataResolver(configuration);
        oidcOpMetadataResolver.init();
        return oidcOpMetadataResolver;
    }

    protected static OIDCProviderMetadata getOidcProviderMetadata(List<ClientAuthenticationMethod> supportedClientAuthenticationMethods)
        throws URISyntaxException {
        OIDCProviderMetadata providerMetadata = new OIDCProviderMetadata(new Issuer("issuer"), List.of(SubjectType.PUBLIC), new URI(""));
        providerMetadata.setIDTokenJWSAlgs(List.of(OidcOpMetadataResolverTestBase.JWS_ALGORITHM));
        providerMetadata.setTokenEndpointAuthMethods(supportedClientAuthenticationMethods);
        return providerMetadata;
    }
}
