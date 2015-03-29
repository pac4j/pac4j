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
package org.scribe.builder.api;

import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.Google2JsonExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

/**
 * This class represents the OAuth API implementation for LinkedIn using OAuth protocol version 2.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedInApi20 extends StateApi20 {
    private static final String AUTHORIZATION_URL = "https://www.linkedin.com/uas/oauth2/authorization?response_type=code&client_id=%s&scope=%s&state=%s&redirect_uri=%s";
    
    @Override
    public String getAccessTokenEndpoint() {
        return "https://www.linkedin.com/uas/oauth2/accessToken?grant_type=authorization_code";
    }
    
    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }
    
    @Override
    public String getAuthorizationUrl(final OAuthConfig config, final String state) {
        Preconditions.checkValidUrl(config.getCallback(),
                                    "Must provide a valid url as callback. LinkedIn does not support OOB");
        return String.format(AUTHORIZATION_URL, config.getApiKey(), OAuthEncoder.encode(config.getScope()),
                             OAuthEncoder.encode(state), OAuthEncoder.encode(config.getCallback()));
    }
    
    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new Google2JsonExtractor();
    }
}
