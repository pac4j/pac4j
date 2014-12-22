/*
  Copyright 2012 - 2014 Jerome Leleu

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
package org.pac4j.oauth.credentials;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;
import org.scribe.model.Token;

/**
 * This class represents an OAuth credentials for OAuth 1.0 &amp; 2.0 : a request token, a token and a verifier.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class OAuthCredentials extends Credentials {
    
    private static final long serialVersionUID = -7705033802712382951L;
    
    private final Token requestToken;
    
    private final String token;
    
    private final String verifier;
    
    public OAuthCredentials(final String verifier, final String clientName) {
        this.requestToken = null;
        this.token = null;
        this.verifier = verifier;
        setClientName(clientName);
    }
    
    public OAuthCredentials(final Token requestToken, final String token, final String verifier, final String clientName) {
        this.requestToken = requestToken;
        this.token = token;
        this.verifier = verifier;
        setClientName(clientName);
    }
    
    public Token getRequestToken() {
        return this.requestToken;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public String getVerifier() {
        return this.verifier;
    }
    
    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "requestToken", this.requestToken, "token", this.token,
                                     "verifier", this.verifier, "clientName", getClientName());
    }
}
