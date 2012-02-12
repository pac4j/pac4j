/*
  Copyright 2012 Jérôme Leleu

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

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.impl.GoogleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a default implementation of an OAuth protocol provider based on the Scribe library. It should work for all OAuth providers.
 * In subclasses, some methods are to be implemented / customized for specific needs depending on the provider.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public abstract class BaseOAuthProvider implements OAuthProvider {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuthProvider.class);
    
    protected static final String REQUEST_TOKEN = "requestToken";
    
    protected OAuthService service;
    
    protected String key;
    
    protected String secret;
    
    protected String callbackUrl;
    
    protected String name;
    
    private boolean initialized = false;
    
    public synchronized void init() {
        if (!initialized) {
            internalInit();
            initialized = true;
        }
    }
    
    /**
     * Internal init of the provider.
     */
    protected abstract void internalInit();
    
    public UserProfile getUserProfile(Token accessToken) {
        String body = sendRequestForProfile(accessToken, getProfileUrl());
        if (body == null) {
            return null;
        }
        return extractUserProfile(body);
    }
    
    /**
     * Retrieve the url of the profile of the authenticated user for this provider.
     * 
     * @return the url of the user profile given by the provider
     */
    protected abstract String getProfileUrl();
    
    /**
     * Make a request to get the profile of the authenticated user for this provider.
     * 
     * @param accessToken
     * @param profileUrl
     * @return the user profile response
     */
    protected String sendRequestForProfile(Token accessToken, String profileUrl) {
        logger.debug("accessToken : {} / profileUrl : {}", accessToken, profileUrl);
        OAuthRequest request = new OAuthRequest(Verb.GET, profileUrl);
        service.signRequest(accessToken, request);
        // for Google
        if (this instanceof GoogleProvider) {
            request.addHeader("GData-Version", "3.0");
        }
        Response response = request.send();
        int code = response.getCode();
        String body = response.getBody();
        logger.debug("response code : {} / response body : {}", code, body);
        if (code != 200) {
            logger.error("Get the user profile failed, code : " + code + " / body : " + body);
            return null;
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
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        if (name == null || "".equals(name)) {
            return getType();
        } else {
            return name;
        }
    }
    
    public String getType() {
        return this.getClass().getName();
    }
    
    public String getKey() {
        return key;
    }
    
    public String getSecret() {
        return secret;
    }
    
    public String getCallbackUrl() {
        return callbackUrl;
    }
}
