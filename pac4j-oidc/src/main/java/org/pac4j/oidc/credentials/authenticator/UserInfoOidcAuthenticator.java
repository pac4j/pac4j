package org.pac4j.oidc.credentials.authenticator;

import static java.util.Optional.ofNullable;

import java.io.IOException;

import javax.naming.AuthenticationException;

import org.pac4j.core.context.WebContext;
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
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
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
    public void validate(final Credentials cred, final WebContext context) {
        init();

        final TokenCredentials credentials = (TokenCredentials) cred;
        final OidcProfileDefinition profileDefinition = new OidcProfileDefinition();
        final OidcProfile profile = (OidcProfile) profileDefinition.newProfile();
        final BearerAccessToken accessToken = new BearerAccessToken(credentials.getToken());
        profile.setAccessToken(accessToken);
        final JWTClaimsSet userInfoClaimsSet = fetchOidcProfile(accessToken);
        ofNullable(userInfoClaimsSet)
            .map(JWTClaimsSet::getClaims)
            .ifPresent(claims -> profileDefinition.convertAndAdd(profile, claims, null));

        // session expiration with token behavior
        profile.setTokenExpirationAdvance(configuration.getTokenExpirationAdvance());

        credentials.setUserProfile(profile);
    }

    private JWTClaimsSet fetchOidcProfile(BearerAccessToken accessToken) {
        final UserInfoRequest userInfoRequest = new UserInfoRequest(configuration.findProviderMetadata().getUserInfoEndpointURI(),
            accessToken);
        final HTTPRequest userInfoHttpRequest = userInfoRequest.toHTTPRequest();
        configuration.configureHttpRequest(userInfoHttpRequest);
        try {
            final HTTPResponse httpResponse = userInfoHttpRequest.send();
            logger.debug("Token response: status={}, content={}", httpResponse.getStatusCode(),
                httpResponse.getContent());
            final UserInfoResponse userInfoResponse = UserInfoResponse.parse(httpResponse);
            if (userInfoResponse instanceof UserInfoErrorResponse) {
                logger.error("Bad User Info response, error={}",
                    ((UserInfoErrorResponse) userInfoResponse).getErrorObject().toJSONObject());
                throw new AuthenticationException();
            } else {
                final UserInfoSuccessResponse userInfoSuccessResponse = (UserInfoSuccessResponse) userInfoResponse;
                final JWTClaimsSet userInfoClaimsSet;
                if (userInfoSuccessResponse.getUserInfo() != null) {
                    userInfoClaimsSet = userInfoSuccessResponse.getUserInfo().toJWTClaimsSet();
                } else {
                    userInfoClaimsSet = userInfoSuccessResponse.getUserInfoJWT().getJWTClaimsSet();
                }
                return userInfoClaimsSet;
            }
        } catch (IOException | ParseException | java.text.ParseException | AuthenticationException e) {
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
