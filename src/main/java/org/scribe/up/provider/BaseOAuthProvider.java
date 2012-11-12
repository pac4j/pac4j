/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.provider;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.BaseOAuthProfile;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.exception.CredentialException;
import org.scribe.up.provider.exception.HttpException;
import org.scribe.up.provider.impl.GoogleProvider;
import org.scribe.up.provider.impl.WordPressProvider;
import org.scribe.up.session.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a default implementation of an OAuth protocol provider based on the Scribe library. It should work for all OAuth providers.
 * In subclasses, some methods are to be implemented / customized for specific needs depending on the provider.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public abstract class BaseOAuthProvider implements OAuthProvider, Cloneable {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuthProvider.class);
    
    protected OAuthService service;
    
    protected String key;
    
    protected String secret;
    
    protected String callbackUrl;
    
    private String type;
    
    // 0,5 second
    protected int connectTimeout = 500;
    
    // 3 seconds
    protected int readTimeout = 3000;
    
    protected String proxyHost = null;
    
    protected int proxyPort = 8080;
    
    private boolean initialized = false;
    
    @Override
    public BaseOAuthProvider clone() {
        final BaseOAuthProvider newProvider = newProvider();
        newProvider.setKey(this.key);
        newProvider.setSecret(this.secret);
        newProvider.setCallbackUrl(this.callbackUrl);
        newProvider.setConnectTimeout(this.connectTimeout);
        newProvider.setReadTimeout(this.readTimeout);
        newProvider.setProxyHost(this.proxyHost);
        newProvider.setProxyPort(this.proxyPort);
        return newProvider;
    }
    
    /**
     * Create a new instance of the provider.
     * 
     * @return A new instance of the provider
     */
    protected abstract BaseOAuthProvider newProvider();
    
    /**
     * Initialize the provider : it's not necessarily to call this method as it's implicitly called by the provider for the main methods of
     * the {@link OAuthProvider} interface.
     */
    public void init() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    internalInit();
                    this.initialized = true;
                }
            }
        }
    }
    
    /**
     * Force (again) the initialization of the provider.
     */
    public synchronized void reinit() {
        internalInit();
        this.initialized = true;
    }
    
    /**
     * Internal init of the provider.
     */
    protected abstract void internalInit();
    
    public UserProfile getUserProfile(final OAuthCredential credential) {
        try {
            return retrieveUserProfile(credential);
        } catch (HttpException e) {
            return null;
        }
    }
    
    /**
     * Retrieve the user profile from OAuth credential.
     * 
     * @param credential
     * @return the user profile object
     * @throws HttpException
     */
    public UserProfile retrieveUserProfile(final OAuthCredential credential) throws HttpException {
        init();
        final Token accessToken = getAccessToken(credential);
        return retrieveUserProfile(accessToken);
    }
    
    /**
     * Retrieve the user profile from the access token (as String).
     * 
     * @param accessToken
     * @return the user profile object
     */
    public UserProfile getUserProfile(final String accessToken) {
        try {
            return retrieveUserProfile(accessToken);
        } catch (HttpException e) {
            return null;
        }
    }
    
    /**
     * Retrieve the user profile from the access token (as String).
     * 
     * @param accessToken
     * @return the user profile object
     * @throws HttpException
     */
    public UserProfile retrieveUserProfile(final String accessToken) throws HttpException {
        init();
        final Token token = new Token(accessToken, "");
        return retrieveUserProfile(token);
    }
    
    /**
     * Retrieve the access token from OAuth credential.
     * 
     * @param credential
     * @return the access token
     */
    protected abstract Token getAccessToken(OAuthCredential credential);
    
    /**
     * Retrieve the user profile from the access token.
     * 
     * @param accessToken
     * @return the user profile object
     * @throws HttpException
     */
    protected UserProfile retrieveUserProfile(final Token accessToken) throws HttpException {
        final String body = sendRequestForData(accessToken, getProfileUrl());
        if (body == null) {
            return null;
        }
        final UserProfile profile = extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        return profile;
    }
    
    /**
     * Add the access token to the profile (as an attribute).
     * 
     * @param profile
     * @param accessToken
     */
    protected void addAccessTokenToProfile(final UserProfile profile, final Token accessToken) {
        if (profile != null) {
            final String token = accessToken.getToken();
            logger.debug("add access_token : {} to profile", token);
            ((BaseOAuthProfile) profile).setAccessToken(token);
        }
    }
    
    /**
     * Retrieve the url of the profile of the authenticated user for this provider.
     * 
     * @return the url of the user profile given by the provider
     */
    protected abstract String getProfileUrl();
    
    /**
     * Make a request to get the data of the authenticated user for this provider.
     * 
     * @param accessToken
     * @param dataUrl
     * @return the user data response
     * @throws HttpException
     */
    protected String sendRequestForData(final Token accessToken, final String dataUrl) throws HttpException {
        logger.debug("accessToken : {} / dataUrl : {}", accessToken, dataUrl);
        final long t0 = System.currentTimeMillis();
        final ProxyOAuthRequest request = new ProxyOAuthRequest(Verb.GET, dataUrl, this.proxyHost, this.proxyPort);
        if (this.connectTimeout != 0) {
            request.setConnectTimeout(this.connectTimeout, TimeUnit.MILLISECONDS);
        }
        if (this.readTimeout != 0) {
            request.setReadTimeout(this.readTimeout, TimeUnit.MILLISECONDS);
        }
        this.service.signRequest(accessToken, request);
        // for Google
        if (this instanceof GoogleProvider) {
            request.addHeader("GData-Version", "3.0");
        } else if (this instanceof WordPressProvider) {
            request.addHeader("Authorization", "Bearer " + accessToken.getToken());
        }
        final Response response = request.send();
        final int code = response.getCode();
        final String body = response.getBody();
        final long t1 = System.currentTimeMillis();
        logger.debug("Request took : " + (t1 - t0) + " ms for : " + dataUrl);
        logger.debug("response code : {} / response body : {}", code, body);
        if (code != 200) {
            logger.error("Failed to get user data, code : " + code + " / body : " + body);
            throw new HttpException(code, body);
        }
        return body;
    }
    
    /**
     * Extract the user profile from the response (JSON, XML...) of the profile url.
     * 
     * @param body
     * @return the user profile object
     */
    protected abstract UserProfile extractUserProfile(String body);
    
    public OAuthCredential getCredential(final UserSession session, final Map<String, String[]> parameters) {
        try {
            return retrieveCredential(session, parameters);
        } catch (CredentialException e) {
            return null;
        }
    }
    
    /**
     * Get the OAuth credential from user session and given parameters.
     * 
     * @param session
     * @param parameters
     * @return the OAuth credential or null if no credential is found
     * @throws CredentialException
     */
    public OAuthCredential retrieveCredential(final UserSession session, final Map<String, String[]> parameters)
        throws CredentialException {
        init();
        boolean errorFound = false;
        CredentialException credentialException = new CredentialException();
        String errorMessage = "";
        for (final String key : CredentialException.ERROR_NAMES) {
            final String[] values = parameters.get(key);
            if (values != null && values.length > 0) {
                errorFound = true;
                String message = values[0];
                errorMessage += key + " : '" + message + "'; ";
                credentialException.setErrorMessage(key, message);
            }
        }
        if (errorFound) {
            logger.error(errorMessage);
            throw credentialException;
        } else {
            return extractCredentialFromParameters(session, parameters);
        }
    }
    
    /**
     * Get credential from user session and given parameters.
     * 
     * @param session
     * @param parameters
     * @return the OAuth credential or null if no credential is found
     */
    protected abstract OAuthCredential extractCredentialFromParameters(UserSession session,
                                                                       Map<String, String[]> parameters);
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public void setSecret(final String secret) {
        this.secret = secret;
    }
    
    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
    
    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String getType() {
        if (StringUtils.isBlank(this.type)) {
            return this.getClass().getSimpleName();
        }
        return this.type;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getSecret() {
        return this.secret;
    }
    
    public String getCallbackUrl() {
        return this.callbackUrl;
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
}
