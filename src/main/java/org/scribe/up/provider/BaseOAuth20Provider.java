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

import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.session.UserSession;
import org.scribe.utils.OAuthEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the common implementation for provider supporting OAuth protocol version 2.0.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public abstract class BaseOAuth20Provider extends BaseOAuthProvider {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuth20Provider.class);
    
    public static final String OAUTH_CODE = "code";
    
    public String getAuthorizationUrl(UserSession session) {
        // no requestToken for OAuth 2.0 -> no need to save it in the user session
        String authorizationUrl = service.getAuthorizationUrl(null);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        return authorizationUrl;
    }
    
    public Token getAccessToken(OAuthCredential credential) {
        // no request token saved in user session and no token (OAuth v2.0)
        String verifier = credential.getVerifier();
        logger.debug("verifier : {}", verifier);
        Verifier providerVerifier = new Verifier(verifier);
        Token accessToken = service.getAccessToken(null, providerVerifier);
        logger.debug("accessToken : {}", accessToken);
        return accessToken;
    }
    
    @Override
    public OAuthCredential extractCredentialFromParameters(UserSession session, Map<String, String[]> parameters) {
        String[] verifiers = parameters.get(OAUTH_CODE);
        if (verifiers != null && verifiers.length == 1) {
            String verifier = OAuthEncoder.decode(verifiers[0]);
            logger.debug("verifier : {}", verifier);
            return new OAuthCredential(null, null, verifier, getType());
        } else {
            logger.error("No credential found");
            return null;
        }
    }
}
