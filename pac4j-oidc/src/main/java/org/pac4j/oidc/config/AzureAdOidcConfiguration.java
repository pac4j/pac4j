package org.pac4j.oidc.config;

import org.pac4j.core.util.HttpUtils;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * AzureAd OpenID Connect configuration.
 *
 * @author Stephen More
 * @since 3.0.0
 */
@Deprecated
public class AzureAdOidcConfiguration extends OidcConfiguration {

    /** AzureAd tenant **/
    private String tenant;

    public AzureAdOidcConfiguration() {
    }

    public AzureAdOidcConfiguration(final OidcConfiguration oidcConfiguration) {
        this.setProviderMetadata(oidcConfiguration.getProviderMetadata());
        this.setClientId(oidcConfiguration.getClientId());
        this.setSecret(oidcConfiguration.getSecret());
        this.setScope(oidcConfiguration.getScope());
        this.setCustomParams(oidcConfiguration.getCustomParams());
        this.setClientAuthenticationMethod(oidcConfiguration.getClientAuthenticationMethod());
        this.setUseNonce(oidcConfiguration.isUseNonce());
        this.setPreferredJwsAlgorithm(oidcConfiguration.getPreferredJwsAlgorithm());
        this.setMaxClockSkew(oidcConfiguration.getMaxClockSkew());
        this.setConnectTimeout(oidcConfiguration.getConnectTimeout());
        this.setReadTimeout(oidcConfiguration.getReadTimeout());
        this.setDiscoveryURI(oidcConfiguration.getDiscoveryURI());
        this.setResourceRetriever(oidcConfiguration.getResourceRetriever());
        this.setResponseType(oidcConfiguration.getResponseType());
        this.setResponseMode(oidcConfiguration.getResponseMode());
        this.setLogoutUrl(oidcConfiguration.getLogoutUrl());

        if (oidcConfiguration instanceof AzureAdOidcConfiguration) {
            final var azureConfig = (AzureAdOidcConfiguration) oidcConfiguration;
            this.setTenant(azureConfig.getTenant());
        }
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("tenant", tenant);

        super.internalInit(forceReinit);
    }

    @Override
    public String getDiscoveryURI() {
        return "https://login.microsoftonline.com/" + tenant + "/.well-known/openid-configuration";
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String makeOauth2TokenRequest(String refreshToken) {
        final var payload = HttpUtils.encodeQueryParam("client_id",this.getClientId())
            + "&" + HttpUtils.encodeQueryParam("client_secret",this.getSecret())
            + "&" + HttpUtils.encodeQueryParam("grant_type","refresh_token")
            + "&" + HttpUtils.encodeQueryParam("refresh_token",refreshToken)
            + "&" + HttpUtils.encodeQueryParam("resource",this.getClientId());

        return payload;
    }
}
