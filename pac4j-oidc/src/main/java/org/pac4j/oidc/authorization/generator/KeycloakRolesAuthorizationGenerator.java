package org.pac4j.oidc.authorization.generator;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.validators.IDTokenClaimsVerifier;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.keycloak.KeycloakOidcProfile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Specific {@link AuthorizationGenerator} to Keycloak.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
@Slf4j
@Getter
@Setter
public class KeycloakRolesAuthorizationGenerator implements AuthorizationGenerator {

    private OidcConfiguration configuration;

    private String clientId;

    /**
     * <p>Constructor for KeycloakRolesAuthorizationGenerator.</p>
     */
    public KeycloakRolesAuthorizationGenerator() {
    }

    /**
     * <p>Constructor for KeycloakRolesAuthorizationGenerator.</p>
     *
     * @param clientId a {@link String} object
     * @deprecated use {@link #KeycloakRolesAuthorizationGenerator(OidcConfiguration)} instead: without the
     * configuration, the access token cannot be validated and no role is generated
     */
    @Deprecated
    public KeycloakRolesAuthorizationGenerator(final String clientId) {
        this.clientId = clientId;
    }

    /**
     * <p>Constructor for KeycloakRolesAuthorizationGenerator.</p>
     *
     * @param configuration a {@link OidcConfiguration} object
     */
    public KeycloakRolesAuthorizationGenerator(final OidcConfiguration configuration) {
        this.configuration = configuration;
        this.clientId = configuration.getClientId();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<UserProfile> generate(final CallContext ctx, final UserProfile profile) {

        if (profile instanceof KeycloakOidcProfile) {
            val accessToken = ((KeycloakOidcProfile) profile).getAccessToken();
            if (accessToken == null) {
                return Optional.of(profile);
            }
            if (configuration == null) {
                LOGGER.warn("No OIDC configuration defined: the Keycloak access token cannot be validated, no role is generated");
                return Optional.of(profile);
            }

            try {
                // the access token is not necessarily bound to the validated ID token: validate it before trusting its roles
                val jwtClaimsSet = validateAccessToken(JWTParser.parse(accessToken.getValue()));

                val realmRolesJsonObject = jwtClaimsSet.getJSONObjectClaim("realm_access");
                if (realmRolesJsonObject != null) {
                    Iterable<String> realmRolesJsonArray = (List<String>) realmRolesJsonObject.get("roles");
                    if (realmRolesJsonArray != null) {
                        realmRolesJsonArray.forEach(role -> profile.addRole(role));
                    }
                }

                if (clientId != null) {
                    val resourceAccess = jwtClaimsSet.getJSONObjectClaim("resource_access");
                    if (resourceAccess != null) {
                        val clientRolesJsonObject = (Map) resourceAccess.get(clientId);
                        if (clientRolesJsonObject != null) {
                            Iterable<String> clientRolesJsonArray = (List<String>) clientRolesJsonObject.get("roles");
                            if (clientRolesJsonArray != null) {
                                clientRolesJsonArray.forEach(profile::addRole);
                            }
                        }
                    }
                }
            } catch (final Exception e) {
                LOGGER.warn("Cannot validate the Keycloak access token or parse its roles", e);
            }
        }

        return Optional.of(profile);
    }

    /**
     * Validate the access token as an ID token, accepting the 'azp' claim as a fallback for the audience:
     * Keycloak does not necessarily add the client to the audience of its access tokens.
     *
     * @param jwt the access token
     * @return the validated claims
     * @throws Exception if the access token cannot be validated
     */
    protected JWTClaimsSet validateAccessToken(final JWT jwt) throws Exception {
        val tokenValidator = configuration.getOpMetadataResolver().getTokenValidator();
        try {
            return tokenValidator.validateIdToken(jwt, null).toJWTClaimsSet();
        } catch (final BadJWTException e) {
            // the same exception is raised for an unsigned JWT: only a signed one had its signature verified
            // before the claims verifier ran, so it is the only one which may enter the fallback
            if (!(jwt instanceof SignedJWT)) {
                throw e;
            }
            val claims = jwt.getJWTClaimsSet();
            val audience = claims.getAudience();
            if (clientId == null || audience == null || audience.isEmpty() || audience.contains(clientId)
                || !clientId.equals(claims.getStringClaim("azp"))) {
                throw e;
            }
            LOGGER.debug("Audience {} does not contain the client id: falling back on the azp claim", audience);
            // re-run all the other checks with an accepted audience
            new IDTokenClaimsVerifier(configuration.getOpMetadataResolver().load().getIssuer(),
                new ClientID(audience.get(0)), null, configuration.getMaxClockSkew()).verify(claims, null);
            return claims;
        }
    }
}
