package org.pac4j.oidc.config;

import org.pac4j.core.util.CommonHelper;

/**
 * AzureAd OpenID Connect configuration.
 *
 * @author Stephen More
 * @since 3.0.0
 */
public class AzureAdOidcConfiguration extends OidcConfiguration {

    /** AzureAd tenant **/
    private String tenant;

    @Override
    protected void internalInit() {
        // checks
        CommonHelper.assertNotBlank("tenant", tenant);

        super.internalInit();
    }

    @Override
    public String getDiscoveryURI() {
        return "https://login.microsoftonline.com/"+tenant+"/.well-known/openid-configuration";
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

}
