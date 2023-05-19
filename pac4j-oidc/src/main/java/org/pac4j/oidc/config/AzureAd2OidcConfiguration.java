package org.pac4j.oidc.config;

import lombok.*;
import lombok.experimental.Accessors;
import org.pac4j.core.util.HttpUtils;
import org.pac4j.oidc.metadata.AzureAdOpMetadataResolver;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.isBlank;

/**
 * Microsoft Azure AD v2 OpenID Connect configuration.
 *
 * @author Charley Wu
 * @since 5.0.0
 */
@ToString(callSuper = true)
@Getter
@Setter
@Accessors(chain = true)
@With
@AllArgsConstructor
public class AzureAd2OidcConfiguration extends OidcConfiguration {

    /** AzureAd tenant **/
    private String tenant;

    /**
     * <p>Constructor for AzureAd2OidcConfiguration.</p>
     */
    public AzureAd2OidcConfiguration() {
    }

    /**
     * <p>Constructor for AzureAd2OidcConfiguration.</p>
     *
     * @param oidcConfiguration a {@link OidcConfiguration} object
     */
    public AzureAd2OidcConfiguration(final OidcConfiguration oidcConfiguration) {
        this.setOpMetadataResolver(oidcConfiguration.getOpMetadataResolver());
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
        if (oidcConfiguration instanceof AzureAd2OidcConfiguration azureAd2OidcConfiguration) {
            this.setTenant(azureAd2OidcConfiguration.getTenant());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        if (isBlank(getTenant())){
            // default value
            setTenant("common");
        }

        if (this.getOpMetadataResolver() == null) {
            assertNotBlank("discoveryURI", getDiscoveryURI());
            this.opMetadataResolver = new AzureAdOpMetadataResolver(this);
            this.opMetadataResolver.init();
        }

        super.internalInit(forceReinit);
    }

    /** {@inheritDoc} */
    @Override
    public String getDiscoveryURI() {
        return "https://login.microsoftonline.com/" + getTenant() + "/v2.0/.well-known/openid-configuration";
    }

    /**
     * <p>makeOauth2TokenRequest.</p>
     *
     * @param refreshToken a {@link String} object
     * @return a {@link String} object
     */
    public String makeOauth2TokenRequest(String refreshToken) {
        var scope = this.getScope();
        if (isBlank(scope)){
            // default values
            scope = "openid profile email";
        }
        val payload = HttpUtils.encodeQueryParam("client_id",this.getClientId())
            + "&" + HttpUtils.encodeQueryParam("client_secret",this.getSecret())
            + "&" + HttpUtils.encodeQueryParam("grant_type","refresh_token")
            + "&" + HttpUtils.encodeQueryParam("refresh_token",refreshToken)
            + "&" + HttpUtils.encodeQueryParam("tenant", this.getTenant())
            + "&" + HttpUtils.encodeQueryParam("scope", scope);

        return payload;
    }
}
