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
package org.scribe.up.addon_to_scribe;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;

/**
 * This class represents the OAuth API implementation for CAS OAuth wrapper.
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
    public String getAccessTokenEndpoint() {
        return this.casServerUrl + "/access_token?";
    }
    
    @Override
    public String getAuthorizationUrl(final OAuthConfig config) {
        return String.format(this.casServerUrl + "/authorize?client_id=%s&redirect_uri=%s", config.getApiKey(),
                             OAuthEncoder.encode(config.getCallback()));
    }
}
