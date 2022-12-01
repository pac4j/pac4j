package org.pac4j.oidc.authorization.generator;

import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oidc.profile.keycloak.KeycloakOidcProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Specific {@link AuthorizationGenerator} to Keycloak.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class KeycloakRolesAuthorizationGenerator implements AuthorizationGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakRolesAuthorizationGenerator.class);

    private String clientId;

    public KeycloakRolesAuthorizationGenerator() {
    }

    public KeycloakRolesAuthorizationGenerator(final String clientId) {
        this.clientId = clientId;
    }

    @Override
    public Optional<UserProfile> generate(final WebContext context, final SessionStore sessionStore, final UserProfile profile) {

        if (profile instanceof KeycloakOidcProfile) {
            try {
                val jwt = SignedJWT.parse(((KeycloakOidcProfile) profile).getAccessToken().getValue());
                val jwtClaimsSet = jwt.getJWTClaimsSet();

                val realmRolesJsonObject = jwtClaimsSet.getJSONObjectClaim("realm_access");
                if (realmRolesJsonObject != null) {
                    val realmRolesJsonArray = (List<String>) realmRolesJsonObject.get("roles");
                    if (realmRolesJsonArray != null) {
                        realmRolesJsonArray.forEach(role -> profile.addRole((String) role));
                    }
                }

                if (clientId != null) {
                    val resourceAccess = jwtClaimsSet.getJSONObjectClaim("resource_access");
                    if (resourceAccess != null) {
                        val clientRolesJsonObject = (Map) resourceAccess.get(clientId);
                        if (clientRolesJsonObject != null) {
                            val clientRolesJsonArray = (List<String>) clientRolesJsonObject.get("roles");
                            if (clientRolesJsonArray != null) {
                                clientRolesJsonArray.forEach(profile::addRole);
                            }
                        }
                    }
                }
            } catch (final Exception e) {
                LOGGER.warn("Cannot parse Keycloak roles", e);
            }
        }

        return Optional.of(profile);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }
}
