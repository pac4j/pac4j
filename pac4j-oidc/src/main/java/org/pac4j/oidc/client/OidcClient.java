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

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.ClientType;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.pac4j.oidc.profile.OidcProfile;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
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
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
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
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import com.nimbusds.openid.connect.sdk.util.DefaultJWTDecoder;
import com.nimbusds.openid.connect.sdk.util.DefaultResourceRetriever;

/**
 * This class is the client to authenticate users with an OpenID Connect 1.0 provider.
 * By default, this implementation relies on the
 * "code" response type. (http://openid.net/specs/openid-connect-core-1_0.html).
 *
 * @author Michael Remond
 * @since 1.7.0
 */
public class OidcClient extends IndirectClient<OidcCredentials, OidcProfile> {

    /* Parameter to indicate to send nonce in the authentication request */
    private static final String USE_NONCE_PARAM = "useNonce";

    /* state attribute name in session */
    private static final String STATE_ATTRIBUTE = "oidcStateAttribute";

    /* nonce attribute name in session */
    private static final String NONCE_ATTRIBUTE = "oidcNonceAttribute";

    /* OpenID client_id */
    private String clientId;

    /* OpenID secret */
    private String secret;

    /* OpenID redirect_uri */
    private URI redirectURI;

    /* discovery URI for fetching OP metadata (http://openid.net/specs/openid-connect-discovery-1_0.html) */
    private String discoveryURI;

    /* Decoder for the JWT ID Token */
    private DefaultJWTDecoder jwtDecoder;

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

    @Override
    public ClientType getClientType() {
        return ClientType.OPENID_CONNECT_PROTOCOL;
    }

    public void setDiscoveryURI(final String discoveryURI) {
        this.discoveryURI = discoveryURI;
    }

    public void setClientID(final String clientID) {
        this.clientId = clientID;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public void setScope(String scope) {
		this.scope = scope;
	}

    public void addCustomParam(final String key, final String value) {
        this.customParams.put(key, value);
    }

    public void setCustomParams(Map<String, String> customParams) {
        CommonHelper.assertNotNull("customParams", customParams);
        this.customParams = customParams;
    }

    @Override
    protected void internalInit() {

        CommonHelper.assertNotBlank(this.clientId, "clientID cannot be blank");
        CommonHelper.assertNotBlank(this.secret, "secret cannot be blank");
        CommonHelper.assertNotBlank(this.discoveryURI, "discoveryURI cannot be blank");

        this.authParams = new HashMap<String, String>();
        
        // add scope
        if(StringUtils.isNotBlank(this.scope)){
        	this.authParams.put("scope", this.scope);
        } else {
	        // default values
	        this.authParams.put("scope", "openid profile email");
        }
        
        this.authParams.put("response_type", "code");
        this.authParams.put("redirect_uri", getCallbackUrl());
        // add custom values
        this.authParams.putAll(this.customParams);
        // Override with required values
        this.authParams.put("client_id", this.clientId);
        this.authParams.put("client_secret", this.secret);

        this._clientID = new ClientID(this.clientId);
        this._secret = new Secret(this.secret);

        JWKSet jwkSet;
        // Download OIDC metadata and Json Web Key Set
        try {
            DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever();
            this.oidcProvider = OIDCProviderMetadata.parse(resourceRetriever.retrieveResource(
                    new URL(this.discoveryURI)).getContent());
            jwkSet = JWKSet.parse(resourceRetriever.retrieveResource(this.oidcProvider.getJWKSetURI().toURL())
                    .getContent());

            this.redirectURI = new URI(getCallbackUrl());
        } catch (Exception e) {
            throw new TechnicalException(e);
        }
        // Get available client authentication method
        ClientAuthenticationMethod method = getClientAuthenticationMethod();
        this.clientAuthentication = getClientAuthentication(method);
        // Init JWT decoder
        this.jwtDecoder = new DefaultJWTDecoder();
        initJwtDecoder(this.jwtDecoder, jwkSet);

    }

    @Override
    protected IndirectClient<OidcCredentials, OidcProfile> newClient() {
        OidcClient client = new OidcClient();
        client.setClientID(this.clientId);
        client.setSecret(this.secret);
        client.setDiscoveryURI(this.discoveryURI);
        client.setAuthParams(this.authParams);

        return client;
    }

    @Override
    protected boolean isDirectRedirection() {
        return true;
    }

    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext context) {

        Map<String, String> params = new HashMap<String, String>(this.authParams);

        // Init state for CSRF mitigation
        State state = new State();
        params.put("state", state.getValue());
        context.setSessionAttribute(STATE_ATTRIBUTE, state);
        // Init nonce for replay attack mitigation
        if (useNonce()) {
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
        String location = this.oidcProvider.getAuthorizationEndpointURI().toString() + "?" + queryString;
        logger.debug("Authentication request url : {}", location);

        return RedirectAction.redirect(location);
    }

    @Override
    protected OidcCredentials retrieveCredentials(final WebContext context) throws RequiresHttpAction {

        // Parse authentication response parameters
        Map<String, String> parameters = toSingleParameter(context.getRequestParameters());
        AuthenticationResponse response;
        try {
            response = AuthenticationResponseParser.parse(this.redirectURI, parameters);
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

        final TokenRequest request = new TokenRequest(this.oidcProvider.getTokenEndpointURI(), this.clientAuthentication,
                new AuthorizationCodeGrant(credentials.getCode(), this.redirectURI));
        HTTPResponse httpResponse;
        try {
            // Token request
            httpResponse = request.toHTTPRequest().send();
            logger.debug("Token response: status={}, content={}", httpResponse.getStatusCode(),
                    httpResponse.getContent());

            final TokenResponse response = OIDCTokenResponseParser.parse(httpResponse);
            if (response instanceof TokenErrorResponse) {
                logger.error("Bad token response, error={}", ((TokenErrorResponse) response).getErrorObject());
                return null;
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
            if (this.oidcProvider.getUserInfoEndpointURI() != null) {
                UserInfoRequest userInfoRequest = new UserInfoRequest(this.oidcProvider.getUserInfoEndpointURI(),
                        accessToken);
                httpResponse = userInfoRequest.toHTTPRequest().send();
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

            // Check ID Token
            final JWTClaimsSet claimsSet = this.jwtDecoder.decodeJWT(oidcTokens.getIDToken());
            if(claimsSet != null){
            	profile.setId(claimsSet.getSubject());
            	profile.addAttributes(claimsSet.getClaims());
            }
            
            if (useNonce()) {
                String nonce = claimsSet.getStringClaim("nonce");
                if (nonce == null || !nonce.equals(context.getSessionAttribute(NONCE_ATTRIBUTE))) {
                    throw new TechnicalException(
                            "A nonce was sent in the authentication request but it is missing or different in the ID Token. "
                                    + "Session expired or possible threat of cross-site request forgery");
                }
            }

            return profile;

        } catch (Exception e) {
            throw new TechnicalException(e);
        }

    }

    /**
     * Returns the first available authentication method from the OP.
     *
     * @return
     */
    private ClientAuthenticationMethod getClientAuthenticationMethod() {
        return this.oidcProvider.getTokenEndpointAuthMethods() != null
                && this.oidcProvider.getTokenEndpointAuthMethods().size() > 0 ? this.oidcProvider
                .getTokenEndpointAuthMethods().get(0) : ClientAuthenticationMethod.getDefault();
    }

    /**
     * Returns <code>true</code> if we want to use a nonce.
     *
     * @return
     */
    private boolean useNonce() {
        return Boolean.parseBoolean(this.authParams.get(USE_NONCE_PARAM));
    }

    /**
     * Add the required verifiers and decrypters to the JWT Decoder based on the JWK set from the OP.
     *
     * @param jwtDecoder
     * @param jwkSet
     */
    private void initJwtDecoder(final DefaultJWTDecoder jwtDecoder, final JWKSet jwkSet) {
        try {
            for (JWK key : jwkSet.getKeys()) {
                if (key.getKeyUse() == KeyUse.SIGNATURE) {
                    jwtDecoder.addJWSVerifier(getVerifier(key));
                } else if (key.getKeyUse() == KeyUse.ENCRYPTION) {
                    jwtDecoder.addJWEDecrypter(getDecrypter(key));
                }
            }
        } catch (Exception e) {
            throw new TechnicalException(e);
        }
    }

    private JWEDecrypter getDecrypter(final JWK key) throws JOSEException {
        if (key instanceof RSAKey) {
            return new RSADecrypter(((RSAKey) key).toRSAPrivateKey());
        }
        return null;
    }

    private JWSVerifier getVerifier(final JWK key) throws JOSEException {
        if (key instanceof RSAKey) {
            return new RSASSAVerifier(((RSAKey) key).toRSAPublicKey());
        } else if (key instanceof ECKey) {
            ECKey ecKey = (ECKey) key;
            return new ECDSAVerifier(ecKey);
        }
        return null;
    }

    /**
     * Returns a configured Client Authentication with method, client_id and secret for the token End Point.
     *
     * @param method
     * @return
     */
    private ClientAuthentication getClientAuthentication(final ClientAuthenticationMethod method) {
        if (ClientAuthenticationMethod.CLIENT_SECRET_POST.equals(method)) {
            return new ClientSecretPost(this._clientID, this._secret);
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(method)) {
            return new ClientSecretBasic(this._clientID, this._secret);
        }
        return null;
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
}
