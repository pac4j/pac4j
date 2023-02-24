package org.pac4j.oidc.authorization.generator;

import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oidc.profile.keycloak.KeycloakOidcProfile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Specific {@link org.pac4j.core.authorization.generator.AuthorizationGenerator} to Keycloak.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
@Slf4j
@Getter
@Setter
public class KeycloakRolesAuthorizationGenerator implements AuthorizationGenerator {

    private String clientId;

    /**
     * <p>Constructor for KeycloakRolesAuthorizationGenerator.</p>
     */
    public KeycloakRolesAuthorizationGenerator() {
    }

    /**
     * <p>Constructor for KeycloakRolesAuthorizationGenerator.</p>
     *
     * @param clientId a {@link java.lang.String} object
     */
    public KeycloakRolesAuthorizationGenerator(final String clientId) {
        this.clientId = clientId;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<UserProfile> generate(final CallContext ctx, final UserProfile profile) {

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
}
