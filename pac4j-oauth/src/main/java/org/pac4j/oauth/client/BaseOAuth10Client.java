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

import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.client.exception.OAuthCredentialsException;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.OAuth10Profile;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.utils.OAuthEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the base implementation for client supporting OAuth protocol version 1.0.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public abstract class BaseOAuth10Client<U extends OAuth10Profile> extends BaseOAuthClient<U> {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuth10Client.class);
    
    public static final String OAUTH_TOKEN = "oauth_token";
    
    public static final String OAUTH_VERIFIER = "oauth_verifier";
    
    public static final String REQUEST_TOKEN = "requestToken";
    
    /**
     * Return the name of the attribute storing in session the request token.
     * 
     * @return the name of the attribute storing in session the request token
     */
    protected String getRequestTokenSessionAttributeName() {
        return getName() + "#" + REQUEST_TOKEN;
    }
    
    @Override
    protected String retrieveAuthorizationUrl(final WebContext context) {
        final Token requestToken = this.service.getRequestToken();
        logger.debug("requestToken : {}", requestToken);
        // save requestToken in user session
        context.setSessionAttribute(getRequestTokenSessionAttributeName(), requestToken);
        final String authorizationUrl = this.service.getAuthorizationUrl(requestToken);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        return authorizationUrl;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected OAuthCredentials getOAuthCredentials(final WebContext context) {
        final String tokenParameter = context.getRequestParameter(OAUTH_TOKEN);
        final String verifierParameter = context.getRequestParameter(OAUTH_VERIFIER);
        if (tokenParameter != null && verifierParameter != null) {
            // get request token from session
            final Token tokenSession = (Token) context.getSessionAttribute(getRequestTokenSessionAttributeName());
            logger.debug("tokenRequest : {}", tokenSession);
            final String token = OAuthEncoder.decode(tokenParameter);
            final String verifier = OAuthEncoder.decode(verifierParameter);
            logger.debug("token : {} / verifier : {}", token, verifier);
            return new OAuthCredentials(tokenSession, token, verifier, getName());
        } else {
            final String message = "No credential found";
            logger.error(message);
            throw new OAuthCredentialsException(message);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Token getAccessToken(final OAuthCredentials credentials) {
        final Token tokenRequest = credentials.getRequestToken();
        final String token = credentials.getToken();
        final String verifier = credentials.getVerifier();
        logger.debug("tokenRequest : {}", tokenRequest);
        logger.debug("token : {}", token);
        logger.debug("verifier : {}", verifier);
        if (tokenRequest == null) {
            final String message = "Token request expired";
            logger.error(message);
            throw new OAuthCredentialsException(message);
        }
        final String savedToken = tokenRequest.getToken();
        logger.debug("savedToken : {}", savedToken);
        if (savedToken == null || !savedToken.equals(token)) {
            final String message = "Token received : " + token + " is different from saved token : " + savedToken;
            logger.error(message);
            throw new OAuthCredentialsException(message);
        }
        final Verifier clientVerifier = new Verifier(verifier);
        final Token accessToken = this.service.getAccessToken(tokenRequest, clientVerifier);
        logger.debug("accessToken : {}", accessToken);
        return accessToken;
    }
    
    @Override
    protected boolean isDirectRedirection() {
        return false;
    }
    
    @Override
    protected void addAccessTokenToProfile(final U profile, final Token accessToken) {
        super.addAccessTokenToProfile(profile, accessToken);
        profile.setAccessSecret(accessToken.getSecret());
    }
}
