package org.pac4j.oidc.profile.creator;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.*;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.profile.OidcProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * OpenID Connect profile creator.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcProfileCreator<U extends OidcProfile> extends InitializableWebObject implements ProfileCreator<OidcCredentials, U> {

    private static final Logger logger = LoggerFactory.getLogger(OidcProfileCreator.class);

    private OidcConfiguration configuration;

    private Class<U> clazz;

    protected IDTokenValidator idTokenValidator;

    public OidcProfileCreator() {}

    public OidcProfileCreator(final OidcConfiguration configuration, final Class<U> clazz) {
        this.configuration = configuration;
        this.clazz = clazz;
    }

    @Override
    protected void internalInit(final WebContext context) {
        assertNotNull("configuration", configuration);
        assertNotNull("clazz", clazz);

        // check algorithms
        final List<JWSAlgorithm> metadataAlgorithms = configuration.getProviderMetadata().getIDTokenJWSAlgs();
        CommonHelper.assertTrue(CommonHelper.isNotEmpty(metadataAlgorithms), "There must at least one JWS algorithm supported on the OpenID Connect provider side");
        final JWSAlgorithm jwsAlgorithm;
        final JWSAlgorithm preferredAlgorithm = configuration.getPreferredJwsAlgorithm();
        if (metadataAlgorithms.contains(preferredAlgorithm)) {
            jwsAlgorithm = preferredAlgorithm;
        } else {
            jwsAlgorithm = metadataAlgorithms.get(0);
            logger.warn("Preferred JWS algorithm: {} not available. Defaulting to: {}", preferredAlgorithm, jwsAlgorithm);
        }

        final ClientID _clientID = new ClientID(configuration.getClientId());
        final Secret _secret = new Secret(configuration.getSecret());
        // Init IDTokenVerifier
        if (CommonHelper.isNotBlank(configuration.getSecret()) && (jwsAlgorithm == JWSAlgorithm.HS256 || jwsAlgorithm == JWSAlgorithm.HS384 || jwsAlgorithm == JWSAlgorithm.HS512)) {
            this.idTokenValidator = createHMACTokenValidator(jwsAlgorithm, _clientID, _secret);
        } else {
            this.idTokenValidator = createRSATokenValidator(jwsAlgorithm, _clientID);
        }
        this.idTokenValidator.setMaxClockSkew(configuration.getMaxClockSkew());
    }

    protected IDTokenValidator createRSATokenValidator(final JWSAlgorithm jwsAlgorithm, final ClientID clientID) {
        try {
            return new IDTokenValidator(configuration.getProviderMetadata().getIssuer(), clientID, jwsAlgorithm,
                    configuration.getProviderMetadata().getJWKSetURI().toURL(), configuration.getResourceRetriever());
        } catch (final MalformedURLException e) {
            throw new TechnicalException(e);
        }
    }

    protected IDTokenValidator createHMACTokenValidator(final JWSAlgorithm jwsAlgorithm, final ClientID clientID, final Secret secret) {
        return new IDTokenValidator(configuration.getProviderMetadata().getIssuer(), clientID, jwsAlgorithm, secret);
    }

    @Override
    @SuppressWarnings("unchecked")
    public U create(final OidcCredentials credentials, final WebContext context) throws HttpAction {

        final BearerAccessToken accessToken = (BearerAccessToken) credentials.getAccessToken();

        // Create profile
        final U profile = (U) ProfileHelper.buildUserProfileByClassCompleteName(clazz.getName());
        profile.setAccessToken(accessToken);
        profile.setIdTokenString(credentials.getIdToken().getParsedString());
        // Check if there is refresh token
        if (credentials.getRefreshToken() != null && !credentials.getRefreshToken().getValue().isEmpty()) {
            profile.setRefreshTokenString(credentials.getRefreshToken().getValue());
            logger.debug("Refresh Token successful retrieved");
        }

        try {
            // User Info request
            if (configuration.getProviderMetadata().getUserInfoEndpointURI() != null) {
                final UserInfoRequest userInfoRequest = new UserInfoRequest(configuration.getProviderMetadata().getUserInfoEndpointURI(), accessToken);
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
                    final UserInfo userInfo = userInfoSuccessResponse.getUserInfo();
                    if (userInfo != null) {
                        profile.addAttributes(userInfo.toJWTClaimsSet().getClaims());
                    }
                }
            }

            final Nonce nonce;
            if (configuration.isUseNonce()) {
                nonce = new Nonce((String) context.getSessionAttribute(OidcConfiguration.NONCE_SESSION_ATTRIBUTE));
            } else {
                nonce = null;
            }
            // Check ID Token
            final IDTokenClaimsSet claimsSet = this.idTokenValidator.validate(credentials.getIdToken(), nonce);
            assertNotNull("claimsSet", claimsSet);
            profile.setId(claimsSet.getSubject());

            return profile;

        } catch (final IOException | ParseException | JOSEException | BadJOSEException e) {
            throw new TechnicalException(e);
        }
    }

    public OidcConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(OidcConfiguration configuration) {
        this.configuration = configuration;
    }

    public Class<U> getClazz() {
        return clazz;
    }

    public void setClazz(Class<U> clazz) {
        this.clazz = clazz;
    }

    public IDTokenValidator getIdTokenValidator() {
        return idTokenValidator;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", configuration, "clazz", clazz);
    }
}
