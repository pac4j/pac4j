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

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

/**
 * This class represents the OAuth API implementation for WordPress. It could be part of the Scribe library.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class WordPressApi extends DefaultApi20 {
    private static final String BASE_URL = "https://public-api.wordpress.com/oauth2/";
    
    private static final String AUTHORIZE_URL = BASE_URL + "authorize?client_id=%s&redirect_uri=%s&response_type=code";
    
    @Override
    public String getAccessTokenEndpoint() {
        return BASE_URL + "token";
    }
    
    @Override
    public String getAuthorizationUrl(final OAuthConfig config) {
        Preconditions.checkValidUrl(config.getCallback(),
                                    "Must provide a valid url as callback. WordPress does not support OOB");
        
        return String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }
    
    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }
    
    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new JsonTokenExtractor();
    }
}
