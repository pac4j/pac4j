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
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.session.UserSession;

/**
 * This interface represents a provider using OAuth protocol. It's the main contract of the project.<br />
 * <br />
 * A provider has a type accessible by the <i>getType()</i> method and could be initialized through the <i>init()</i> method.<br />
 * A provider supports off course the OAuth authentication process through the <i>getAuthorizationUrl(UserSession session)</i>,
 * <i>getCredential(UserSession session, Map&lt;String, String[]&gt; parameters)</i> and <i>getAccessToken(OAuthCredential credential)</i>
 * methods, {@link org.scribe.up.session.UserSession} is the session of the current user, {@link org.scribe.up.credential.OAuthCredential}
 * is the OAuth credential.<br />
 * A provider can finally retrieve the user profile from an OAuth access token through the <i>getUserProfile(Token accessToken)</i> method.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public interface OAuthProvider {
    
    /**
     * Initialize the provider.
     */
    public void init();
    
    /**
     * Get the type of the provider.
     * 
     * @return the type of the provider
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
     * Get the OAuth credential from user session and given parameters.
     * 
     * @session
     * @param parameters
     * @return the OAuth credential or null if no credential is found
     */
    public OAuthCredential getCredential(UserSession session, Map<String, String[]> parameters);
    
    /**
     * Retrieve the access token from OAuth credential.
     * 
     * @param credential
     * @return the access token
     */
    public Token getAccessToken(OAuthCredential credential);
    
    /**
     * Retrieve the user profile from the access token.
     * 
     * @param accessToken
     * @return the user profile object
     */
    public UserProfile getUserProfile(Token accessToken);
}
