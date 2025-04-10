package org.pac4j.oidc.profile.creator;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.*;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.exceptions.UserInfoErrorResponseException;
import org.pac4j.oidc.profile.OidcProfile;
import org.pac4j.oidc.profile.OidcProfileDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static org.pac4j.core.credentials.authenticator.Authenticator.ALWAYS_VALIDATE;
import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;
import static org.pac4j.core.util.CommonHelper.*;

/**
 * OpenID Connect profile creator.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcProfileCreator extends ProfileDefinitionAware implements ProfileCreator {

    private static final Logger logger = LoggerFactory.getLogger(OidcProfileCreator.class);

    protected OidcConfiguration configuration;

    protected OidcClient client;

    public OidcProfileCreator(final OidcConfiguration configuration, final OidcClient client) {
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("configuration", configuration);

        defaultProfileDefinition(new OidcProfileDefinition());

        if (!configuration.isCallUserInfoEndpoint()
            && (client.getAuthenticator() == null || client.getAuthenticator() == ALWAYS_VALIDATE)) {
            // prevent creating a profile from an unvalidated access token
            throw new TechnicalException("You cannot disable the call to the UserInfo endpoint " +
                "without providing an authenticator");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<UserProfile> create(final Credentials credentials, final WebContext context, final SessionStore sessionStore) {
        init();

        OidcCredentials oidcCredentials = null;
        AccessToken accessToken = null;

        final boolean regularOidcFlow = credentials instanceof OidcCredentials;
        if (regularOidcFlow) {
            oidcCredentials = (OidcCredentials) credentials;
            accessToken = oidcCredentials.getAccessToken();
        } else {
            // we assume the access token only has been passed: it can be a bearer call (HTTP client)
            final var token = ((TokenCredentials) credentials).getToken();
            accessToken = new BearerAccessToken(token);
        }

        // Create profile
        final var profile = (OidcProfile) getProfileDefinition().newProfile();
        profile.setAccessToken(accessToken);

        if (oidcCredentials != null) {
            if (oidcCredentials.getIdToken() != null){
                profile.setIdTokenString(oidcCredentials.getIdToken().getParsedString());
            }
            // Check if there is a refresh token
            final var refreshToken = oidcCredentials.getRefreshToken();
            if (refreshToken != null && !refreshToken.getValue().isEmpty()) {
                profile.setRefreshToken(refreshToken);
                logger.debug("Refresh Token successful retrieved");
            }
        }

        try {

            final Nonce nonce;
            if (configuration.isUseNonce()) {
                nonce = new Nonce((String) sessionStore.get(context, client.getNonceSessionAttributeName()).orElse(null));
            } else {
                nonce = null;
            }
            // Check ID Token
            if (oidcCredentials != null && oidcCredentials.getIdToken() != null) {
                final var claimsSet = configuration.findTokenValidator().validate(oidcCredentials.getIdToken(), nonce);
                assertNotNull("claimsSet", claimsSet);
                profile.setId(ProfileHelper.sanitizeIdentifier(claimsSet.getSubject()));

                // keep the session ID if provided
                final var sid = (String) claimsSet.getClaim(Pac4jConstants.OIDC_CLAIM_SESSIONID);
                if (isNotBlank(sid)) {
                    configuration.findLogoutHandler().recordSession(context, sessionStore, sid);
                }
            }

            if (configuration.isCallUserInfoEndpoint()) {
                final var uri = configuration.findProviderMetadata().getUserInfoEndpointURI();
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
                for (final var entry : oidcCredentials.getIdToken().getJWTClaimsSet().getClaims().entrySet()) {
                    final var key = entry.getKey();
                    final var value = entry.getValue();
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
            throw new TechnicalException(e);
        }
    }

    public void callUserInfoEndpoint(final URI userInfoEndpointUri, final AccessToken accessToken, final UserProfile profile)
        throws IOException, ParseException, java.text.ParseException, UserInfoErrorResponseException {
        if (userInfoEndpointUri != null && accessToken != null) {
            final var userInfoRequest = new UserInfoRequest(userInfoEndpointUri, accessToken);
            final var userInfoHttpRequest = userInfoRequest.toHTTPRequest();
            configuration.configureHttpRequest(userInfoHttpRequest);
            final var httpResponse = userInfoHttpRequest.send();
            logger.debug("User info response: status={}, content={}", httpResponse.getStatusCode(),
                httpResponse.getContent());

            final var userInfoResponse = UserInfoResponse.parse(httpResponse);
            if (userInfoResponse instanceof UserInfoErrorResponse) {
                final var error = ((UserInfoErrorResponse) userInfoResponse).getErrorObject();
                logger.error("Bad User Info response, error={}", error);

                throw new UserInfoErrorResponseException(error.toString());
            } else {
                final var userInfoSuccessResponse = (UserInfoSuccessResponse) userInfoResponse;
                final JWTClaimsSet userInfoClaimsSet;
                if (userInfoSuccessResponse.getUserInfo() != null) {
                    userInfoClaimsSet = userInfoSuccessResponse.getUserInfo().toJWTClaimsSet();
                } else {
                    userInfoClaimsSet = userInfoSuccessResponse.getUserInfoJWT().getJWTClaimsSet();
                }
                if (userInfoClaimsSet != null) {
                    final String subject = userInfoClaimsSet.getSubject();
                    if (isBlank(profile.getId()) && isNotBlank(subject)) {
                        profile.setId(ProfileHelper.sanitizeIdentifier(subject));
                    }
                    getProfileDefinition().convertAndAdd(profile, userInfoClaimsSet.getClaims(), null);
                } else {
                    logger.warn("Cannot retrieve claims from user info");
                }
            }
        }
    }

    private void collectClaimsFromAccessTokenIfAny(final OidcCredentials credentials,
                                                   final Nonce nonce, OidcProfile profile) {
        try {
            final AccessToken accessToken = credentials.getAccessToken();
            if (accessToken != null) {
                var accessTokenJwt = JWTParser.parse(accessToken.getValue());
                var accessTokenClaims = configuration.findTokenValidator().validate(accessTokenJwt, nonce);

                // add attributes of the access token if they don't already exist
                for (final var entry : accessTokenClaims.toJWTClaimsSet().getClaims().entrySet()) {
                    final var key = entry.getKey();
                    final var value = entry.getValue();
                    if (!JwtClaims.SUBJECT.equals(key) && profile.getAttribute(key) == null) {
                        getProfileDefinition().convertAndAdd(profile, PROFILE_ATTRIBUTE, key, value);
                    }
                }
            }
        } catch (final ParseException | java.text.ParseException | JOSEException | BadJOSEException e) {
            logger.debug(e.getMessage(), e);
        } catch (final Exception e) {
            throw new TechnicalException(e);
        }
    }
}
