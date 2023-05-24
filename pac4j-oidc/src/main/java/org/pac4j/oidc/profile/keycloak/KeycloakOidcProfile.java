package org.pac4j.oidc.profile.keycloak;

import org.pac4j.oidc.profile.OidcProfile;

import java.io.Serial;

/**
 * <p>This class is the user profile for Keycloak (using OpenID Connect protocol) with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oidc.client.KeycloakOidcClient}.</p>
 *
 * @author Julio Arrebola
 * @version 2.0.0
 */
public class KeycloakOidcProfile extends OidcProfile {

    @Serial
    private static final long serialVersionUID = 8895149078141040113L;
}
