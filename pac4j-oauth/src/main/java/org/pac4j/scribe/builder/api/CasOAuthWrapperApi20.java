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
package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.AccessTokenExtractor;
import com.github.scribejava.core.extractors.JsonTokenExtractor;
import com.github.scribejava.core.extractors.TokenExtractor20Impl;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.utils.OAuthEncoder;

/**
 * This class represents the OAuth API implementation for the CAS OAuth wrapper.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class CasOAuthWrapperApi20 extends DefaultApi20 {
    
    private final String casServerUrl;
    
    private final boolean springSecurityCompliant;
    
    public CasOAuthWrapperApi20(final String casServerUrl, final boolean springSecurityCompliant) {
        this.casServerUrl = casServerUrl;
        this.springSecurityCompliant = springSecurityCompliant;
    }
    
    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        if (this.springSecurityCompliant) {
            return new JsonTokenExtractor();
        } else {
            return new TokenExtractor20Impl();
        }
    }
    
    @Override
    public String getAccessTokenEndpoint() {
        return this.casServerUrl + "/accessToken?";
    }
    
    @Override
    public String getAuthorizationUrl(final OAuthConfig config) {
        return String.format(this.casServerUrl + "/authorize?" + "response_type=code&client_id=%s&redirect_uri=%s",
                             config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }
    
    @Override
    public Verb getAccessTokenVerb() {
        if (this.springSecurityCompliant) {
            return Verb.PUT;
        } else {
            return Verb.POST;
        }
    }
}
