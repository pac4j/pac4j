package org.pac4j.oidc.profile.creator;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Request;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.exceptions.OidcConfigurationException;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.exceptions.UserInfoErrorResponseException;
import org.pac4j.oidc.profile.OidcProfile;
import org.pac4j.oidc.profile.OidcProfileDefinition;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static org.pac4j.core.credentials.authenticator.Authenticator.ALWAYS_VALIDATE;
import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;
import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * OpenID Connect profile creator.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@Slf4j
public class OidcProfileCreator extends ProfileDefinitionAware implements ProfileCreator {

    protected OidcConfiguration configuration;

    protected OidcClient client;

    /**
     * <p>Constructor for OidcProfileCreator.</p>
     *
     * @param configuration a {@link OidcConfiguration} object
     * @param client a {@link OidcClient} object
     */
    public OidcProfileCreator(final OidcConfiguration configuration, final OidcClient client) {
        this.configuration = configuration;
        this.client = client;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("configuration", configuration);

        setProfileDefinitionIfUndefined(new OidcProfileDefinition());

        if (!configuration.isCallUserInfoEndpoint()
            && (client.getAuthenticator() == null || client.getAuthenticator() == ALWAYS_VALIDATE)) {
            // prevent creating a profile from an unvalidated access token
            throw new OidcConfigurationException("You cannot disable the call to the UserInfo endpoint " +
                "without providing an authenticator");
        }
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<UserProfile> create(final CallContext ctx, final Credentials credentials) {
        init();

        OidcCredentials oidcCredentials = null;
        AccessToken accessToken = null;
        // credentials were obtained from a refresh token
        boolean refreshedCredentials = false;
        val regularOidcFlow = credentials instanceof OidcCredentials;
        if (regularOidcFlow) {
            oidcCredentials = (OidcCredentials) credentials;
            accessToken = oidcCredentials.toAccessToken();
            refreshedCredentials = oidcCredentials.isRefreshedCredentials();
        } else {
            // we assume the access token only has been passed: it can be a bearer call (HTTP client)
            val token = ((TokenCredentials) credentials).getToken();
            accessToken = new BearerAccessToken(token);
        }

        // Create profile
        val profile = (OidcProfile) getProfileDefinition().newProfile();
        profile.setAccessToken(accessToken);

        if (oidcCredentials != null) {
            if (oidcCredentials.getIdToken() != null) {
                profile.setIdTokenString(oidcCredentials.toIdToken().getParsedString());
            }
            // Check if there is a refresh token
            val refreshToken = oidcCredentials.toRefreshToken();
            if (refreshToken != null && !refreshToken.getValue().isEmpty()) {
                profile.setRefreshToken(refreshToken);
                LOGGER.debug("Refresh Token successful retrieved");
            }
        }

        try {

            final Nonce nonce;
            // skipRefreshedNonce is true when the token was created with the refresh token and we don't want to check nonce
            // in idToken in this case
            val skipRefreshedNonce = refreshedCredentials && !configuration.isUseNonceOnRefresh();
            if (configuration.isUseNonce() && !skipRefreshedNonce) {
                nonce = new Nonce((String) ctx.sessionStore().get(ctx.webContext(), client.getNonceSessionAttributeName()).orElse(null));
            } else {
                nonce = null;
            }
            // Check ID Token
            if (oidcCredentials != null && oidcCredentials.getIdToken() != null) {
                val claimsSet = configuration.getOpMetadataResolver().getTokenValidator()
                    .validateIdToken(oidcCredentials.toIdToken(), nonce);
                assertNotNull("claimsSet", claimsSet);
                profile.setId(ProfileHelper.sanitizeIdentifier(claimsSet.getSubject()));

                // keep the session ID if provided
                val sid = (String) claimsSet.getClaim(Pac4jConstants.OIDC_CLAIM_SESSIONID);
                val sessionLogoutHandler = client.findSessionLogoutHandler();
                if (StringUtils.isNotBlank(sid) && sessionLogoutHandler != null) {
                    sessionLogoutHandler.recordSession(ctx, sid);
                }
            }

            if (configuration.isCallUserInfoEndpoint()) {
                final var uri = configuration.getOpMetadataResolver().load().getUserInfoEndpointURI();
                try {
                    callUserInfoEndpoint(uri, accessToken, profile);
                } catch (final UserInfoErrorResponseException e) {
                    // bearer call -> no profile returned
                    if (!regularOidcFlow) {
                        return Optional.empty();
                    }
                }
            }

            // add attributes of the ID token if they don't already exist
            if (oidcCredentials != null && oidcCredentials.getIdToken() != null) {
                for (val entry : oidcCredentials.toIdToken().getJWTClaimsSet().getClaims().entrySet()) {
                    val key = entry.getKey();
                    val value = entry.getValue();
                    // it's not the subject and this attribute does not already exist, add it
                    if (!JwtClaims.SUBJECT.equals(key) && profile.getAttribute(key) == null) {
                        getProfileDefinition().convertAndAdd(profile, PROFILE_ATTRIBUTE, key, value);
                    }
                }
            }

            if (oidcCredentials != null && configuration.isIncludeAccessTokenClaimsInProfile()) {
                collectClaimsFromAccessTokenIfAny(oidcCredentials, nonce, profile);
            }

            // session expiration with token behavior
            profile.setTokenExpirationAdvance(configuration.getTokenExpirationAdvance());

            return Optional.of(profile);
        } catch (final IOException | ParseException | JOSEException | BadJOSEException | java.text.ParseException e) {
            throw new OidcException(e);
        }
    }

    public void callUserInfoEndpoint(final URI userInfoEndpointUri, final AccessToken accessToken, final UserProfile profile)
        throws IOException, ParseException, java.text.ParseException, UserInfoErrorResponseException {
        val opMetadata = configuration.getOpMetadataResolver().load();
        if (opMetadata.getUserInfoEndpointURI() != null && accessToken != null) {
            Request userInfoRequest = new UserInfoRequest(opMetadata.getUserInfoEndpointURI(), accessToken);
            val userInfoHttpRequest = userInfoRequest.toHTTPRequest();
            configuration.configureHttpRequest(userInfoHttpRequest);
            val httpResponse = userInfoHttpRequest.send();
            LOGGER.debug("User info response: status={}, content={}", httpResponse.getStatusCode(),
                httpResponse.getContent());

            val userInfoResponse = UserInfoResponse.parse(httpResponse);
            if (userInfoResponse instanceof UserInfoErrorResponse) {
                final var error = ((UserInfoErrorResponse) userInfoResponse).getErrorObject();
                LOGGER.error("Bad User Info response, error={}", error);

                throw new UserInfoErrorResponseException(error.toString());
            } else {
                val userInfoSuccessResponse = (UserInfoSuccessResponse) userInfoResponse;
                final JWTClaimsSet userInfoClaimsSet;
                if (userInfoSuccessResponse.getUserInfo() != null) {
                    userInfoClaimsSet = userInfoSuccessResponse.getUserInfo().toJWTClaimsSet();
                } else {
                    userInfoClaimsSet = userInfoSuccessResponse.getUserInfoJWT().getJWTClaimsSet();
                }
                if (userInfoClaimsSet != null) {
                    final String subject = userInfoClaimsSet.getSubject();
                    if (StringUtils.isBlank(profile.getId()) && StringUtils.isNotBlank(subject)) {
                        profile.setId(ProfileHelper.sanitizeIdentifier(subject));
                    }
                    getProfileDefinition().convertAndAdd(profile, userInfoClaimsSet.getClaims(), null);
                } else {
                    LOGGER.warn("Cannot retrieve claims from user info");
                }
            }
        }
    }

    private void collectClaimsFromAccessTokenIfAny(final OidcCredentials credentials,
                                                   final Nonce nonce, UserProfile profile) {
        try {
            var accessToken = credentials.toAccessToken();
            if (accessToken != null) {
                var accessTokenJwt = JWTParser.parse(accessToken.getValue());
                var accessTokenClaims = configuration.getOpMetadataResolver().getTokenValidator().validateIdToken(accessTokenJwt, nonce);

                // add attributes of the access token if they don't already exist
                for (val entry : accessTokenClaims.toJWTClaimsSet().getClaims().entrySet()) {
                    val key = entry.getKey();
                    val value = entry.getValue();
                    if (!JwtClaims.SUBJECT.equals(key) && profile.getAttribute(key) == null) {
                        getProfileDefinition().convertAndAdd(profile, PROFILE_ATTRIBUTE, key, value);
                    }
                }
            }
        } catch (final ParseException | java.text.ParseException | JOSEException | BadJOSEException e) {
            LOGGER.debug(e.getMessage(), e);
        } catch (final Exception e) {
            throw new OidcException(e);
        }
    }
}
