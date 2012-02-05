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

import java.util.Map;

import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.session.UserSession;
import org.scribe.utils.OAuthEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the common implementation for provider supporting OAuth protocol v1.0.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public abstract class BaseOAuth10Provider extends BaseOAuthProvider {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuth10Provider.class);
    
    private static final String OAUTH_TOKEN = "oauth_token";
    
    private static final String OAUTH_VERIFIER = "oauth_verifier";
    
    public String getAuthorizationUrl(UserSession session) {
        Token requestToken = service.getRequestToken();
        logger.debug("requestToken : {}", requestToken);
        // save requestToken in session
        session.setAttribute(getName() + "#" + REQUEST_TOKEN, requestToken);
        String authorizationUrl = service.getAuthorizationUrl(requestToken);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        return authorizationUrl;
    }
    
    public Token getAccessToken(UserSession session, OAuthCredential credential) {
        String token = credential.getToken();
        String verifier = credential.getVerifier();
        logger.debug("token : {}", token);
        logger.debug("verifier : {}", verifier);
        // get tokenRequest from session
        Token tokenRequest = (Token) session.getAttribute(getName() + "#" + REQUEST_TOKEN);
        logger.debug("tokenRequest : {}", tokenRequest);
        if (tokenRequest == null) {
            throw new OAuthException("Token request expired");
        }
        String savedToken = tokenRequest.getToken();
        logger.debug("savedToken : {}", savedToken);
        if (savedToken == null || !savedToken.equals(token)) {
            throw new OAuthException("Token received : " + token + " is different from saved token : " + savedToken);
        }
        Verifier providerVerifier = new Verifier(verifier);
        Token accessToken = service.getAccessToken(tokenRequest, providerVerifier);
        logger.debug("accessToken : {}", accessToken);
        return accessToken;
    }
    
    public OAuthCredential extractCredentialFromParameters(Map<String, String[]> parameters) {
        String[] tokens = parameters.get(OAUTH_TOKEN);
        String[] verifiers = parameters.get(OAUTH_VERIFIER);
        if (tokens != null && tokens.length == 1 && verifiers != null && verifiers.length == 1) {
            return new OAuthCredential(OAuthEncoder.decode(tokens[0]), OAuthEncoder.decode(verifiers[0]));
        }
        return null;
    }
}
