package org.pac4j.oidc.credentials.authenticator;

import static java.util.Optional.ofNullable;
import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import java.io.IOException;
import java.util.Map;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfile;
import org.pac4j.oidc.profile.OidcProfileDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;

/**
 * The OpenId Connect authenticator by user info.
 *
 * @author Rakesh Sarangi
 * @since 3.5.0
 */
public class UserInfoOidcAuthenticator extends InitializableObject implements Authenticator {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoOidcAuthenticator.class);

    private OidcConfiguration configuration;

    public UserInfoOidcAuthenticator() {}

    public UserInfoOidcAuthenticator(final OidcConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("configuration", configuration);
    }

    @Override
    public void validate(final Credentials cred, final WebContext context, final SessionStore sessionStore) {
        init();

        final var credentials = (TokenCredentials) cred;
        final var profileDefinition = new OidcProfileDefinition();
        final var profile = (OidcProfile) profileDefinition.newProfile();
        final var accessToken = new BearerAccessToken(credentials.getToken());
        profile.setAccessToken(accessToken);
        final var userInfoClaimsSet = fetchOidcProfile(accessToken);
        ofNullable(userInfoClaimsSet)
            .map(JWTClaimsSet::getClaims)
            .ifPresent(claims -> collectProfileClaims(profileDefinition, profile, claims));

        // session expiration with token behavior
        profile.setTokenExpirationAdvance(configuration.getTokenExpirationAdvance());

        credentials.setUserProfile(profile);
    }

    protected void collectProfileClaims(final OidcProfileDefinition profileDefinition,
                                        final OidcProfile profile,
                                        final Map<String, Object> claims) {
        claims.forEach((name, value) -> {
            if (configuration.getMappedClaims().containsKey(name)) {
                var actualName = configuration.getMappedClaims().get(name);
                logger.debug("Mapping claim {} as {} with values {} to profile", name, actualName, value);
                profileDefinition.convertAndAdd(profile, PROFILE_ATTRIBUTE, actualName, value);
            } else {
                logger.debug("Adding claim {} to profile with values {}", name, value);
                profileDefinition.convertAndAdd(profile, PROFILE_ATTRIBUTE, name, value);
            }
        });
    }

    private JWTClaimsSet fetchOidcProfile(BearerAccessToken accessToken) {
        final var userInfoRequest = new UserInfoRequest(configuration.findProviderMetadata().getUserInfoEndpointURI(),
            accessToken);
        final var userInfoHttpRequest = userInfoRequest.toHTTPRequest();
        configuration.configureHttpRequest(userInfoHttpRequest);
        try {
            final var httpResponse = userInfoHttpRequest.send();
            logger.debug("Token response: status={}, content={}", httpResponse.getStatusCode(),
                httpResponse.getContent());
            final var userInfoResponse = UserInfoResponse.parse(httpResponse);
            if (userInfoResponse instanceof UserInfoErrorResponse) {
                throw new TechnicalException("Bad User Info response, error="
                    + ((UserInfoErrorResponse) userInfoResponse).getErrorObject().toJSONObject());
            } else {
                final var userInfoSuccessResponse = (UserInfoSuccessResponse) userInfoResponse;
                final JWTClaimsSet userInfoClaimsSet;
                if (userInfoSuccessResponse.getUserInfo() != null) {
                    userInfoClaimsSet = userInfoSuccessResponse.getUserInfo().toJWTClaimsSet();
                } else {
                    userInfoClaimsSet = userInfoSuccessResponse.getUserInfoJWT().getJWTClaimsSet();
                }
                return userInfoClaimsSet;
            }
        } catch (IOException | ParseException | java.text.ParseException e) {
            throw new TechnicalException(e);
        }
    }

    public OidcConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final OidcConfiguration configuration) {
        this.configuration = configuration;
    }
}
