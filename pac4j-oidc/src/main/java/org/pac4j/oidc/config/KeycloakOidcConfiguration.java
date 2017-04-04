package org.pac4j.oidc.config;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

/**
 * Keycloak OpenID Connect configuration.
 *
 * @author Julio Arrebola
 * @since 2.0.0
 */
public class KeycloakOidcConfiguration extends OidcConfiguration {
    
    /** Keycloak auth realm **/
    private String realm;
    /** Keycloak server base uri **/
    private String baseUri;
    
    @Override
    protected void internalInit(final WebContext context) {
        // checks
        CommonHelper.assertNotBlank("realm", realm);
        CommonHelper.assertNotBlank("baseUri", baseUri);
        
        super.internalInit(context);
    }
    
    @Override
    public String getDiscoveryURI() {
       return baseUri+"/realms/"+realm+"/.well-known/openid-configuration";
    }
    
    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }
    
}
