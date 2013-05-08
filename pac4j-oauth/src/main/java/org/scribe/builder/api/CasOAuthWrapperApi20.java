/*
  Copyright 2012 - 2013 Jerome Leleu

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
package org.scribe.builder.api;

import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;

/**
 * This class represents the OAuth API implementation for the CAS OAuth wrapper.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class CasOAuthWrapperApi20 extends DefaultApi20 {
    
    private final String casServerUrl;
    
    public CasOAuthWrapperApi20(final String casServerUrl) {
        this.casServerUrl = casServerUrl;
    }
    
    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new JsonTokenExtractor();
    }

    @Override
    public String getAccessTokenEndpoint() {
        return this.casServerUrl + "/accessToken?";
    }
    
    @Override
    public String getAuthorizationUrl(final OAuthConfig config) {
        return String.format(this.casServerUrl + "/authorize?" + 
                "response_type=code&client_id=%s&redirect_uri=%s",
                config.getApiKey(),
                OAuthEncoder.encode(config.getCallback()));
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.PUT;
    }
}
