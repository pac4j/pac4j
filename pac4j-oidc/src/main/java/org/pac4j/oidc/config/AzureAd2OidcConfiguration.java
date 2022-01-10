package org.pac4j.oidc.config;

import org.pac4j.core.util.HttpUtils;

import static org.pac4j.core.util.CommonHelper.isBlank;

/**
 * Microsoft Azure AD v2 OpenID Connect configuration.
 *
 * @author Charley Wu
 * @since 5.0.0
 */
@SuppressWarnings( "deprecation" )
public class AzureAd2OidcConfiguration extends AzureAdOidcConfiguration {

    public AzureAd2OidcConfiguration() {
    }

    public AzureAd2OidcConfiguration(final OidcConfiguration oidcConfiguration) {
        super(oidcConfiguration);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        if (isBlank(getTenant())){
            // default value
            setTenant("common");
        }
        super.internalInit(forceReinit);
    }

    @Override
    public String getDiscoveryURI() {
        return "https://login.microsoftonline.com/" + getTenant() + "/v2.0/.well-known/openid-configuration";
    }

    @Override
    public String makeOauth2TokenRequest(String refreshToken) {
        var scope = this.getScope();
        if (isBlank(scope)){
            // default values
            scope = "openid profile email";
        }
        final var payload = HttpUtils.encodeQueryParam("client_id",this.getClientId())
            + "&" + HttpUtils.encodeQueryParam("client_secret",this.getSecret())
            + "&" + HttpUtils.encodeQueryParam("grant_type","refresh_token")
            + "&" + HttpUtils.encodeQueryParam("refresh_token",refreshToken)
            + "&" + HttpUtils.encodeQueryParam("tenant", this.getTenant())
            + "&" + HttpUtils.encodeQueryParam("scope", scope);

        return payload;
    }
}
