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
package org.scribe.up.credential;

/**
 * This class represents an OAuth credential : a token and a verifier associated to a provider type.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public class OAuthCredential {
    
    protected String token;
    
    protected String verifier;
    
    protected String providerType;
    
    public OAuthCredential(String token, String verifier, String providerType) {
        this.token = token;
        this.verifier = verifier;
        this.providerType = providerType;
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
        return "[token:" + token + ",verifier:" + verifier + ",providerType:" + providerType + "]";
    }
}
