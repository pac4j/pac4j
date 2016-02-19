/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.oidc.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.ClientType;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.profile.OidcProfile;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.DefaultResourceRetriever;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.http.ResourceRetriever;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;

/**
 * This class is the client to authenticate users with an OpenID Connect 1.0 provider.
 * By default, this implementation relies on the
 * "code" response type. (http://openid.net/specs/openid-connect-core-1_0.html).
 *
 * @author Michael Remond
 * @since 1.7.0
 */
public class OidcClient extends IndirectClient<OidcCredentials, OidcProfile> {

    /* state attribute name in session */
    private static final String STATE_ATTRIBUTE = "oidcStateAttribute";

    /* nonce attribute name in session */
    private static final String NONCE_ATTRIBUTE = "oidcNonceAttribute";

    /* default max clock skew */
    private static final int DEFAULT_MAX_CLOCK_SKEW = 30;

    /* OpenID client_id */
    private String clientId;

    /* OpenID secret */
    private String secret;

    /* OpenID redirect_uri */
    private URI redirectURI;

    /* discovery URI for fetching OP metadata (http://openid.net/specs/openid-connect-discovery-1_0.html) */
    private String discoveryURI;

    /* ID Token verifier */
    private IDTokenValidator idTokenValidator;

    /* OIDC metadata */
    private OIDCProviderMetadata oidcProvider;

    /* Map containing the parameters for configuring all aspects of the OpenID Connect integration */
    private Map<String, String> authParams;

    /* Scope */
    private String scope;

    /* Map containing user defined parameters */
    private Map<String, String> customParams = new HashMap<String, String>();

    /* client authentication object at the token End Point (basic, form or JWT) */
    private ClientAuthentication clientAuthentication;

    /* clientID object */
    private ClientID _clientID;

    /* secret object */
    private Secret _secret;

    /* use nonce? */
    private boolean useNonce;

    /* Preferred JWS algorithm */
    private JWSAlgorithm preferredJwsAlgorithm;

    /* max clock skew in seconds */
    private int maxClockSkew = DEFAULT_MAX_CLOCK_SKEW;

    /* timeouts for token and userinfo requests */
    private int connectTimeout = HttpConstants.DEFAULT_CONNECT_TIMEOUT;

    private int readTimeout = HttpConstants.DEFAULT_READ_TIMEOUT;

    public OidcClient() { }

    public OidcClient(final String clientId, final String secret, final String discoveryURI) {
        this.clientId = clientId;
        this.secret = secret;
        this.discoveryURI = discoveryURI;
    }

    @Override
    public ClientType getClientType() {
        return ClientType.OPENID_CONNECT_PROTOCOL;
    }

    public void setDiscoveryURI(final String discoveryURI) {
        this.discoveryURI = discoveryURI;
    }
    
    public String getDiscoveryURI() {
        return this.discoveryURI;
    }

    public void setClientID(final String clientID) {
        this.clientId = clientID;
    }

    public String getClientID() {
        return this.clientId;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }
    
    public String getSecret() {
        return this.secret;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return this.scope;
    }

    public IDTokenValidator getIdTokenValidator() {
		return idTokenValidator;
	}

    public void addCustomParam(final String key, final String value) {
        this.customParams.put(key, value);
    }

    public void setCustomParams(Map<String, String> customParams) {
        CommonHelper.assertNotNull("customParams", customParams);
        this.customParams = customParams;
    }

    public Map<String, String> getCustomParams() {
        return this.customParams;
    }

    public int getConnectTimeout() {
        return connectTimeout;
	}

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Map<String, String> getAuthParams() {
        return this.authParams;
    }

    public OIDCProviderMetadata getProviderMetadata() {
        return this.oidcProvider;
    }
    
    public URI getRedirectURI() {
        return this.redirectURI;
    }

    public ClientAuthentication getClientAuthentication() {
        return this.clientAuthentication;
    }

    @Override
    protected void internalInit(final WebContext context) {

        CommonHelper.assertNotBlank(getClientID(), "clientID cannot be blank");
        CommonHelper.assertNotBlank(getSecret(), "secret cannot be blank");
        CommonHelper.assertNotBlank(getDiscoveryURI(), "discoveryURI cannot be blank");

        this.authParams = new HashMap<String, String>();

        // add scope
        if(StringUtils.isNotBlank(getScope())){
            this.authParams.put("scope", getScope());
        } else {
            // default values
            this.authParams.put("scope", "openid profile email");
        }

        this.authParams.put("response_type", "code");
        final String computedCallbackUrl = computeFinalCallbackUrl(context);
        this.authParams.put("redirect_uri", computedCallbackUrl);
        // add custom values
        this.authParams.putAll(getCustomParams());
        // Override with required values
        this.authParams.put("client_id", getClientID());
        this.authParams.put("client_secret", getSecret());

        this._clientID = new ClientID(getClientID());
        this._secret = new Secret(getSecret());

        try {
            // Download OIDC metadata
            ResourceRetriever resourceRetriever = createResourceRetriever();
            this.oidcProvider = OIDCProviderMetadata.parse(resourceRetriever.retrieveResource(
                    new URL(getDiscoveryURI())).getContent());
            this.redirectURI = new URI(computedCallbackUrl);
            // check algorithms
            final List<JWSAlgorithm> algorithms = getProviderMetadata().getIDTokenJWSAlgs();
            CommonHelper.assertTrue(algorithms != null && algorithms.size() > 0, "There must at least one JWS algorithm supported on the OpenID Connect provider side");
            final JWSAlgorithm jwsAlgorithm;
            if (algorithms.contains(getPreferredJwsAlgorithm())) {
                jwsAlgorithm = getPreferredJwsAlgorithm();
            } else {
                jwsAlgorithm = algorithms.get(0);
                logger.warn("Preferred JWS algorithm: {} not available. Defaulting to: {}", getPreferredJwsAlgorithm(), jwsAlgorithm);
            }
            // Init IDTokenVerifier
            if (CommonHelper.isNotBlank(getSecret()) && (jwsAlgorithm == JWSAlgorithm.HS256 || jwsAlgorithm == JWSAlgorithm.HS384 || jwsAlgorithm == JWSAlgorithm.HS512)) {
                this.idTokenValidator = createHMACTokenValidator(jwsAlgorithm, _clientID, _secret);
            } else {
                this.idTokenValidator = createRSATokenValidator(jwsAlgorithm, _clientID);
            }
            getIdTokenValidator().setMaxClockSkew(getMaxClockSkew());

        } catch (final IOException | ParseException | URISyntaxException e) {
            throw new TechnicalException(e);
        }

        final ClientAuthenticationMethod method = getProviderMetadata().getTokenEndpointAuthMethods() != null
                && getProviderMetadata().getTokenEndpointAuthMethods().size() > 0 ? getProviderMetadata()
                .getTokenEndpointAuthMethods().get(0) : ClientAuthenticationMethod.getDefault();

        if (ClientAuthenticationMethod.CLIENT_SECRET_POST.equals(method)) {
            this.clientAuthentication = new ClientSecretPost(this._clientID, this._secret);
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(method)) {
            this.clientAuthentication = new ClientSecretBasic(this._clientID, this._secret);
        }
    }

	protected IDTokenValidator createRSATokenValidator(
			final JWSAlgorithm jwsAlgorithm, ClientID clientID) throws MalformedURLException {
		return new IDTokenValidator(getProviderMetadata().getIssuer(), clientID, jwsAlgorithm, getProviderMetadata().getJWKSetURI().toURL());
	}

	protected IDTokenValidator createHMACTokenValidator(
			final JWSAlgorithm jwsAlgorithm, final ClientID clientID, final Secret secret) {
		return new IDTokenValidator(getProviderMetadata().getIssuer(), clientID, jwsAlgorithm, secret);
	}

    protected ResourceRetriever createResourceRetriever() {
        return new DefaultResourceRetriever(getConnectTimeout(), getReadTimeout());
    }

    @Override
    protected IndirectClient<OidcCredentials, OidcProfile> newClient() {
        OidcClient client = new OidcClient();
        client.setClientID(getClientID());
        client.setSecret(getSecret());
        client.setDiscoveryURI(getDiscoveryURI());
        client.setAuthParams(getAuthParams());
        client.setUseNonce(isUseNonce());
        client.setPreferredJwsAlgorithm(getPreferredJwsAlgorithm());
        client.setMaxClockSkew(getMaxClockSkew());
        client.setConnectTimeout(getConnectTimeout());
        client.setReadTimeout(getReadTimeout());
        return client;
    }

    @Override
    protected boolean isDirectRedirection() {
        return false;
    }

    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext context) {

        Map<String, String> params = new HashMap<String, String>(getAuthParams());

        // Init state for CSRF mitigation
        State state = new State();
        params.put("state", state.getValue());
        context.setSessionAttribute(STATE_ATTRIBUTE, state);
        // Init nonce for replay attack mitigation
        if (isUseNonce()) {
            Nonce nonce = new Nonce();
            params.put("nonce", nonce.getValue());
            context.setSessionAttribute(NONCE_ATTRIBUTE, nonce.getValue());
        }

        // Build authentication request query string
        String queryString;
        try {
            queryString = AuthenticationRequest.parse(params).toQueryString();
        } catch (Exception e) {
            throw new TechnicalException(e);
        }
        String location = getProviderMetadata().getAuthorizationEndpointURI().toString() + "?" + queryString;
        logger.debug("Authentication request url : {}", location);

        return RedirectAction.redirect(location);
    }

    @Override
    protected OidcCredentials retrieveCredentials(final WebContext context) throws RequiresHttpAction {

        // Parse authentication response parameters
        Map<String, String> parameters = toSingleParameter(context.getRequestParameters());
        AuthenticationResponse response;
        try {
            response = AuthenticationResponseParser.parse(getRedirectURI(), parameters);
        } catch (ParseException e) {
            throw new TechnicalException(e);
        }

        if (response instanceof AuthenticationErrorResponse) {
            logger.error("Bad authentication response, error={}",
                    ((AuthenticationErrorResponse) response).getErrorObject());
            return null;
        }

        logger.debug("Authentication response successful, get authorization code");
        AuthenticationSuccessResponse successResponse = (AuthenticationSuccessResponse) response;

        // state value must be equal
        if (!successResponse.getState().equals(context.getSessionAttribute(STATE_ATTRIBUTE))) {
            throw new TechnicalException("State parameter is different from the one sent in authentication request. "
                    + "Session expired or possible threat of cross-site request forgery");
        }
        // Get authorization code
        AuthorizationCode code = successResponse.getAuthorizationCode();

        return new OidcCredentials(code, getName());
    }

    @Override
    protected OidcProfile retrieveUserProfile(final OidcCredentials credentials, final WebContext context) {

        final TokenRequest request = buildTokenRequest(credentials);
        HTTPResponse httpResponse;
        try {
            // Token request
            HTTPRequest tokenHttpRequest = request.toHTTPRequest();
            tokenHttpRequest.setConnectTimeout(getConnectTimeout());
            tokenHttpRequest.setReadTimeout(getReadTimeout());
            httpResponse = tokenHttpRequest.send();
            logger.debug("Token response: status={}, content={}", httpResponse.getStatusCode(),
                    httpResponse.getContent());

            final TokenResponse response = OIDCTokenResponseParser.parse(httpResponse);
            if (response instanceof TokenErrorResponse) {
                final String msg = "Bad token response, error=" + ((TokenErrorResponse) response).getErrorObject();
                logger.error(msg);
                throw new TechnicalException(msg);
            }
            logger.debug("Token response successful");
            final OIDCTokenResponse tokenSuccessResponse = (OIDCTokenResponse) response;
            final OIDCTokens oidcTokens = tokenSuccessResponse.getOIDCTokens();
            final BearerAccessToken accessToken = (BearerAccessToken) oidcTokens.getAccessToken();

            // Create Oidc profile
            OidcProfile profile = new OidcProfile(accessToken);
            profile.setIdTokenString(oidcTokens.getIDTokenString());

            // User Info request
            UserInfo userInfo = null;
            if (getProviderMetadata().getUserInfoEndpointURI() != null) {
                UserInfoRequest userInfoRequest = buildUserInfoRequest(accessToken);
                HTTPRequest userInfoHttpRequest = userInfoRequest.toHTTPRequest();
                userInfoHttpRequest.setConnectTimeout(getConnectTimeout());
                userInfoHttpRequest.setReadTimeout(getReadTimeout());
                httpResponse = userInfoHttpRequest.send();
                logger.debug("Token response: status={}, content={}", httpResponse.getStatusCode(),
                        httpResponse.getContent());

                UserInfoResponse userInfoResponse = UserInfoResponse.parse(httpResponse);

                if (userInfoResponse instanceof UserInfoErrorResponse) {
                    logger.error("Bad User Info response, error={}",
                            ((UserInfoErrorResponse) userInfoResponse).getErrorObject());
                } else {
                    UserInfoSuccessResponse userInfoSuccessResponse = (UserInfoSuccessResponse) userInfoResponse;
                    userInfo = userInfoSuccessResponse.getUserInfo();
                    if(userInfo != null){
                    	profile.addAttributes(userInfo.toJWTClaimsSet().getClaims());
                    }
                }
            }

            final Nonce nonce;
            if (isUseNonce()) {
                nonce = new Nonce((String) context.getSessionAttribute(NONCE_ATTRIBUTE));
            } else {
                nonce = null;
            }
            // Check ID Token
            final IDTokenClaimsSet claimsSet = getIdTokenValidator().validate(oidcTokens.getIDToken(), nonce);
            CommonHelper.assertNotNull("claimsSet", claimsSet);
            profile.setId(claimsSet.getSubject());

            return profile;

        } catch (Exception e) {
            throw new TechnicalException(e);
        }

    }

    /**
     * @param credentials
     * @return the TokenRequest object that will be used to query the OIDC Token endpoint.
     */
    protected TokenRequest buildTokenRequest(OidcCredentials credentials) {
        return new TokenRequest(getProviderMetadata().getTokenEndpointURI(), getClientAuthentication(),
                new AuthorizationCodeGrant(credentials.getCode(), getRedirectURI()));
    }

    /**
     * @param accessToken
     * @return The UserInfoRequest object that will be used to query the OIDC UserInfo endpoint
     */
    protected UserInfoRequest buildUserInfoRequest(BearerAccessToken accessToken) {
        return new UserInfoRequest(getProviderMetadata().getUserInfoEndpointURI(), accessToken);
    }

    private Map<String, String> toSingleParameter(final Map<String, String[]> requestParameters) {
        Map<String, String> map = new HashMap<String, String>();
        for (Entry<String, String[]> entry : requestParameters.entrySet()) {
            map.put(entry.getKey(), entry.getValue()[0]);
        }
        return map;
    }

    private void setAuthParams(final Map<String, String> authParams) {
        this.authParams = authParams;
    }

    public JWSAlgorithm getPreferredJwsAlgorithm() {
        return this.preferredJwsAlgorithm;
    }

    public void setPreferredJwsAlgorithm(JWSAlgorithm preferredJwsAlgorithm) {
        this.preferredJwsAlgorithm = preferredJwsAlgorithm;
    }

    public boolean isUseNonce() {
        return this.useNonce;
    }

    public void setUseNonce(boolean useNonce) {
        this.useNonce = useNonce;
    }

    public int getMaxClockSkew() {
        return this.maxClockSkew;
    }

    public void setMaxClockSkew(int maxClockSkew) {
        this.maxClockSkew = maxClockSkew;
    }
}
