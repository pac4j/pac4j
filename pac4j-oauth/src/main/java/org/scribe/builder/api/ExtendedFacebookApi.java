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

import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

/**
 * This class allow to add the Facebook state parameter to authorization URL through the overloaded method getAuthorizationUrl.
 * 
 * @author Mehdi BEN HAJ ABBES
 * @since 1.2.0
 */
public final class ExtendedFacebookApi extends StateApi20 {
    
    private static final String AUTHORIZE_URL_WITH_STATE = "https://www.facebook.com/v2.2/dialog/oauth?client_id=%s&redirect_uri=%s&state=%s";
    private static final String SCOPED_AUTHORIZE_URL_WITH_STATE = AUTHORIZE_URL_WITH_STATE + "&scope=%s";
    
    @Override
    public String getAccessTokenEndpoint() {
        return "https://graph.facebook.com/v2.2/oauth/access_token";
    }
    
    @Override
    public String getAuthorizationUrl(final OAuthConfig config, final String state) {
        Preconditions.checkEmptyString(config.getCallback(),
                                       "Must provide a valid url as callback. Facebook does not support OOB");
        
        // Append scope if present
        if (config.hasScope()) {
            return String.format(SCOPED_AUTHORIZE_URL_WITH_STATE, config.getApiKey(),
                                 OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(state),
                                 OAuthEncoder.encode(config.getScope()));
        } else {
            return String.format(AUTHORIZE_URL_WITH_STATE, config.getApiKey(),
                                 OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(state));
        }
    }
}
