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
 * This interface represents a provider using OAuth protocol. It's the main contract of the project. A provider has to have a name
 * accessible by the <i>getName()</i> method and could be initialized throught the <i>init()</i> method. A provider supports off course the
 * OAuth authentication process through the <i>getAuthorizationUrl(UserSession session)</i> and <i>getAccessToken(UserSession session,
 * OAuthCredential credential)</i> methods, <b>UserSession</b> is the session of the current user. A provider can extract an
 * <b>OAuthCredential</b> from a map of parameters : <i>extractCredentialFromParameters(Map<String, String[]> parameters)</i> method and get
 * a user profile from an OAuth access token : <i>getUserProfile(Token accessToken)</i> method.
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
     * Get the type of the provider.
     * 
     * @return
     */
    public String getType();
    
    /**
     * Get the authorization url.
     * 
     * @param session
     * @return the authorization url
     */
    public String getAuthorizationUrl(UserSession session);
    
    /**
     * Retrieve the access token from OAuth credential.
     * 
     * @param session
     * @param credential
     * @return the access token
     */
    public Token getAccessToken(UserSession session, OAuthCredential credential);
    
    /**
     * Retrieve the user profile from the access token.
     * 
     * @param accessToken
     * @return the user profile object
     */
    public UserProfile getUserProfile(Token accessToken);
    
    /**
     * Extract the OAuth credential from given parameters.
     * 
     * @param parameters
     * @return the OAuth credential or null if no credential is found.
     */
    public OAuthCredential extractCredentialFromParameters(Map<String, String[]> parameters);
}
