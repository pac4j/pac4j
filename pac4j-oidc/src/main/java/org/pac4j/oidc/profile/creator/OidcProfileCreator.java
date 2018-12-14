package org.pac4j.oidc.profile.creator;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.*;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.profile.OidcProfile;
import org.pac4j.oidc.profile.OidcProfileDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;
import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * OpenID Connect profile creator.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcProfileCreator<U extends OidcProfile> extends ProfileDefinitionAware<U> implements ProfileCreator<OidcCredentials, U> {

    private static final Logger logger = LoggerFactory.getLogger(OidcProfileCreator.class);

    protected OidcConfiguration configuration;

    protected TokenValidator tokenValidator;

    public OidcProfileCreator(final OidcConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void internalInit() {
        assertNotNull("configuration", configuration);

        if (tokenValidator == null) {
            tokenValidator = new TokenValidator(configuration);
        }

        defaultProfileDefinition(new OidcProfileDefinition<>());
    }

    @Override
    @SuppressWarnings("unchecked")
    public U create(final OidcCredentials credentials, final WebContext context) {
        init();

        final AccessToken accessToken = credentials.getAccessToken();

        // Create profile
        final U profile = getProfileDefinition().newProfile();
        profile.setAccessToken(accessToken);
        final JWT idToken = credentials.getIdToken();
        profile.setIdTokenString(idToken.getParsedString());
        // Check if there is a refresh token
        final RefreshToken refreshToken = credentials.getRefreshToken();
        if (refreshToken != null && !refreshToken.getValue().isEmpty()) {
            profile.setRefreshToken(refreshToken);
            logger.debug("Refresh Token successful retrieved");
        }

        try {

            final Nonce nonce;
            if (configuration.isUseNonce()) {
                nonce = new Nonce((String) context.getSessionStore().get(context, OidcConfiguration.NONCE_SESSION_ATTRIBUTE));
            } else {
                nonce = null;
            }
            // Check ID Token
            final IDTokenClaimsSet claimsSet = this.tokenValidator.validate(idToken, nonce);
            assertNotNull("claimsSet", claimsSet);
            profile.setId(ProfileHelper.sanitizeIdentifier(profile, claimsSet.getSubject()));

            // User Info request
            if (configuration.findProviderMetadata().getUserInfoEndpointURI() != null && accessToken != null) {
                final UserInfoRequest userInfoRequest = new UserInfoRequest(configuration.findProviderMetadata().getUserInfoEndpointURI(),
                    (BearerAccessToken) accessToken);
                final HTTPRequest userInfoHttpRequest = userInfoRequest.toHTTPRequest();
                userInfoHttpRequest.setConnectTimeout(configuration.getConnectTimeout());
                userInfoHttpRequest.setReadTimeout(configuration.getReadTimeout());
                final HTTPResponse httpResponse = userInfoHttpRequest.send();
                logger.debug("Token response: status={}, content={}", httpResponse.getStatusCode(),
                        httpResponse.getContent());

                final UserInfoResponse userInfoResponse = UserInfoResponse.parse(httpResponse);
                if (userInfoResponse instanceof UserInfoErrorResponse) {
                    logger.error("Bad User Info response, error={}",
                            ((UserInfoErrorResponse) userInfoResponse).getErrorObject());
                } else {
                    final UserInfoSuccessResponse userInfoSuccessResponse = (UserInfoSuccessResponse) userInfoResponse;
                    final JWTClaimsSet userInfoClaimsSet;
                    if (userInfoSuccessResponse.getUserInfo() != null) {
                        userInfoClaimsSet = userInfoSuccessResponse.getUserInfo().toJWTClaimsSet();
                    } else {
                        userInfoClaimsSet = userInfoSuccessResponse.getUserInfoJWT().getJWTClaimsSet();
                    }
                    getProfileDefinition().convertAndAdd(profile, userInfoClaimsSet.getClaims(), null);
                }
            }

            // add attributes of the ID token if they don't already exist
            for (final Map.Entry<String, Object> entry : idToken.getJWTClaimsSet().getClaims().entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();
                // it's not the subject and this attribute does not already exist, add it
                if (!JwtClaims.SUBJECT.equals(key) && profile.getAttribute(key) == null) {
                    getProfileDefinition().convertAndAdd(profile, PROFILE_ATTRIBUTE, key, value);
                }
            }

            // session expiration with token behavior
            profile.setTokenExpirationAdvance(configuration.getTokenExpirationAdvance());

            return profile;

        } catch (final IOException | ParseException | JOSEException | BadJOSEException | java.text.ParseException e) {
            throw new TechnicalException(e);
        }
    }
}
