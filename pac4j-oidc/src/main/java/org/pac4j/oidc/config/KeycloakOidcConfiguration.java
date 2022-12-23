package org.pac4j.oidc.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import lombok.experimental.Accessors;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * Keycloak OpenID Connect configuration.
 *
 * @author Julio Arrebola
 * @since 2.0.0
 */
@ToString(callSuper = true)
@Getter
@Setter
@Accessors(chain = true)
@With
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakOidcConfiguration extends OidcConfiguration {

    /** Keycloak auth realm **/
    private String realm;
    /** Keycloak server base uri **/
    private String baseUri;

    @Override
    protected void internalInit(final boolean forceReinit) {
        // checks
        assertNotBlank("realm", realm);
        assertNotBlank("baseUri", baseUri);

        super.internalInit(forceReinit);
    }

    @Override
    public String getDiscoveryURI() {
        return baseUri+"/realms/"+realm+"/.well-known/openid-configuration";
    }
}
