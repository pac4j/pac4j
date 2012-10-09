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

import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

/**
 * This class allow to add the Facebook state parameter to authorization URL through the overloaded method getAuthorizationUrl
 * 
 * @author Mehdi BEN HAJ ABBES
 * @since 1.2.0
 */
public final class ExtendedFacebookApi extends FacebookApi {
    
    private static final String AUTHORIZE_URL_WITH_STATE = "https://www.facebook.com/dialog/oauth?client_id=%s&redirect_uri=%s&state=%s";
    private static final String SCOPED_AUTHORIZE_URL_WITH_STATE = AUTHORIZE_URL_WITH_STATE + "&scope=%s";
    
    public String getAuthorizationUrl(final OAuthConfig config, final String facebookState) {
        Preconditions.checkValidUrl(config.getCallback(),
                                    "Must provide a valid url as callback. Facebook does not support OOB");
        
        // Append scope if present
        if (config.hasScope()) {
            return String.format(SCOPED_AUTHORIZE_URL_WITH_STATE, config.getApiKey(),
                                 OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(facebookState),
                                 OAuthEncoder.encode(config.getScope()));
        } else {
            return String.format(AUTHORIZE_URL_WITH_STATE, config.getApiKey(),
                                 OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(facebookState));
        }
    }
}
