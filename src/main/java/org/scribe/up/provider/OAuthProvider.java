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

import org.scribe.model.Token;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.session.UserSession;

/**
 * This interface represents a provider using OAuth protocol.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public interface OAuthProvider {
    
    /**
     * Initialize the provider.
     */
    public void init();
    
    /**
     * Get the name of the provider.
     */
    public String getName();
    
    /**
     * Get the authorization url.
     * 
     * @param session
     * @return
     */
    public String getAuthorizationUrl(UserSession session);
    
    /**
     * Retrieve the access token from OAuth credential.
     * 
     * @param session
     * @param credential
     * @return
     */
    public Token getAccessToken(UserSession session, OAuthCredential credential);
    
    /**
     * Retrieve the user profile from the access token.
     * 
     * @param accessToken
     * @return
     */
    public UserProfile getUserProfile(Token accessToken);
    
    /**
     * Extract the OAuth credential from given parameters. Return null if no credential is found.
     * 
     * @param parameters
     * @return
     */
    public OAuthCredential extractCredentialFromParameters(Map<String, String[]> parameters);
}
