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
package org.pac4j.oauth.client;

import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Mechanism;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.exception.OAuthCredentialsException;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.OAuth10Profile;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a base implementation for an OAuth protocol client based on the Scribe library. It should work for all OAuth clients. In
 * subclasses, some methods are to be implemented / customized for specific needs depending on the client.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public abstract class BaseOAuthClient<U extends OAuth20Profile> extends BaseClient<OAuthCredentials, U> {

    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuthClient.class);

    protected OAuthService service;

    protected String key;

    protected String secret;

    protected boolean tokenAsHeader = false;

    // 0,5 second
    protected int connectTimeout = 500;

    // 2 seconds
    protected int readTimeout = 2000;

    protected String proxyHost = null;

    protected int proxyPort = 8080;

    @Override
    protected void internalInit() {
        CommonHelper.assertNotBlank("key", this.key);
        CommonHelper.assertNotBlank("secret", this.secret);
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
    }

    @Override
    public BaseOAuthClient<U> clone() {
        final BaseOAuthClient<U> newClient = (BaseOAuthClient<U>) super.clone();
        newClient.setKey(this.key);
        newClient.setSecret(this.secret);
        newClient.setConnectTimeout(this.connectTimeout);
        newClient.setReadTimeout(this.readTimeout);
        newClient.setProxyHost(this.proxyHost);
        newClient.setProxyPort(this.proxyPort);
        return newClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext context) {
        try {
            return RedirectAction.redirect(retrieveAuthorizationUrl(context));
        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    protected abstract String retrieveAuthorizationUrl(final WebContext context);

    /**
     * {@inheritDoc}
     */
    @Override
    protected OAuthCredentials retrieveCredentials(final WebContext context) {
        // check if the authentication has been cancelled
        if (hasBeenCancelled(context)) {
            logger.debug("authentication has been cancelled by user");
            return null;
        }
        // check errors
        try {
            boolean errorFound = false;
            final OAuthCredentialsException oauthCredentialsException = new OAuthCredentialsException(
                    "Failed to retrieve OAuth credentials, error parameters found");
            String errorMessage = "";
            for (final String key : OAuthCredentialsException.ERROR_NAMES) {
                final String value = context.getRequestParameter(key);
                if (value != null) {
                    errorFound = true;
                    errorMessage += key + " : '" + value + "'; ";
                    oauthCredentialsException.setErrorMessage(key, value);
                }
            }
            if (errorFound) {
                logger.error(errorMessage);
                throw oauthCredentialsException;
            } else {
                return getOAuthCredentials(context);
            }
        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Return if the authentication has been cancelled.
     * 
     * @param context the web context.
     * @return if the authentication has been cancelled.
     */
    protected abstract boolean hasBeenCancelled(WebContext context);

    /**
     * Get the OAuth credentials from the web context.
     * 
     * @param context the web context
     * @return the OAuth credentials
     */
    protected abstract OAuthCredentials getOAuthCredentials(final WebContext context);

    /**
     * {@inheritDoc}
     */
    @Override
    protected U retrieveUserProfile(final OAuthCredentials credentials, final WebContext context) {
        try {
            final Token token = getAccessToken(credentials);
            return retrieveUserProfileFromToken(token);
        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Get the user profile from the access token.
     * 
     * @param accessToken the access token
     * @return the user profile
     */
    public U getUserProfile(final String accessToken) {
        init();
        try {
            final Token token = new Token(accessToken, "");
            return retrieveUserProfileFromToken(token);
        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Get the access token from OAuth credentials.
     * 
     * @param credentials credentials
     * @return the access token
     */
    protected abstract Token getAccessToken(OAuthCredentials credentials);

    /**
     * Retrieve the user profile from the access token.
     * 
     * @param accessToken the access token
     * @return the user profile
     */
    protected U retrieveUserProfileFromToken(final Token accessToken) {
        final String body = sendRequestForData(accessToken, getProfileUrl(accessToken));
        if (body == null) {
            throw new HttpCommunicationException("Not data found for accessToken : " + accessToken);
        }
        final U profile = extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        return profile;
    }

    /**
     * Retrieve the url of the profile of the authenticated user for the provider.
     *
     * @param accessToken only used when constructing dynamic urls from data in the token
     * @return the url of the user profile given by the provider
     */
    protected abstract String getProfileUrl(final Token accessToken);

    /**
     * Make a request to get the data of the authenticated user for the provider.
     * 
     * @param accessToken the access token
     * @param dataUrl url of the data
     * @return the user data response
     */
    protected String sendRequestForData(final Token accessToken, final String dataUrl) {
        logger.debug("accessToken : {} / dataUrl : {}", accessToken, dataUrl);
        final long t0 = System.currentTimeMillis();
        final ProxyOAuthRequest request = createProxyRequest(dataUrl);
        this.service.signRequest(accessToken, request);
        // Let the client to decide if the token should be in header
        if (this.isTokenAsHeader()) {
            request.addHeader("Authorization", "Bearer " + accessToken.getToken());
        }
        final Response response = request.send();
        final int code = response.getCode();
        final String body = response.getBody();
        final long t1 = System.currentTimeMillis();
        logger.debug("Request took : " + (t1 - t0) + " ms for : " + dataUrl);
        logger.debug("response code : {} / response body : {}", code, body);
        if (code != 200) {
            logger.error("Failed to get data, code : " + code + " / body : " + body);
            throw new HttpCommunicationException(code, body);
        }
        return body;
    }

    /**
     * Make a request to the OAuth provider to access a protected resource. The profile should contain a valid access token (and secret if
     * needed).
     * 
     * @param profile user profile
     * @param dataUrl url of the data
     * @return the body of the requested resource
     */
    public String sendRequestForData(final OAuth10Profile profile, final String dataUrl) {
        final String secret = profile.getAccessSecret();
        final Token accessToken = new Token(profile.getAccessToken(), secret == null ? "" : secret);
        return sendRequestForData(accessToken, dataUrl);
    }

    /**
     * Create a proxy request.
     * 
     * @param url url of the data
     * @return a proxy request
     */
    protected ProxyOAuthRequest createProxyRequest(final String url) {
        return new ProxyOAuthRequest(Verb.GET, url, this.connectTimeout, this.readTimeout, this.proxyHost,
                this.proxyPort);
    }

    /**
     * Extract the user profile from the response (JSON, XML...) of the profile url.
     * 
     * @param body the response body
     * @return the user profile object
     */
    protected abstract U extractUserProfile(String body);

    /**
     * Add the access token to the profile (as an attribute).
     * 
     * @param profile the user profile
     * @param accessToken the access token
     */
    protected void addAccessTokenToProfile(final U profile, final Token accessToken) {
        if (profile != null) {
            final String token = accessToken.getToken();
            logger.debug("add access_token : {} to profile", token);
            profile.setAccessToken(token);
        }
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getKey() {
        return this.key;
    }

    public String getSecret() {
        return this.secret;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public String getProxyHost() {
        return this.proxyHost;
    }

    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return this.proxyPort;
    }

    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public boolean isTokenAsHeader() {
        return tokenAsHeader;
    }

    public void setTokenAsHeader(boolean tokenAsHeader) {
        this.tokenAsHeader = tokenAsHeader;
    }

    @Override
    public Mechanism getMechanism() {
        return Mechanism.OAUTH_PROTOCOL;
    }
}
