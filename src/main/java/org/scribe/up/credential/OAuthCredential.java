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
package org.scribe.up.credential;

import java.io.Serializable;

import org.scribe.model.Token;

/**
 * This class represents an OAuth credential : a request token, a token and a verifier associated to a provider type.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class OAuthCredential implements Serializable {
    
    private static final long serialVersionUID = -3590688958662634837L;
    
    private final Token requestToken;
    
    private final String token;
    
    private final String verifier;
    
    private final String providerType;
    
    public OAuthCredential(final Token requestToken, final String token, final String verifier,
                           final String providerType) {
        this.requestToken = requestToken;
        this.token = token;
        this.verifier = verifier;
        this.providerType = providerType;
    }
    
    public Token getRequestToken() {
        return requestToken;
    }
    
    public String getToken() {
        return token;
    }
    
    public String getVerifier() {
        return verifier;
    }
    
    public String getProviderType() {
        return providerType;
    }
    
    @Override
    public String toString() {
        return "OAuthCredential{requestToken:" + this.requestToken + ",token:" + this.token + ",verifier:"
               + this.verifier + ",providerType:" + this.providerType + "}";
    }
}
