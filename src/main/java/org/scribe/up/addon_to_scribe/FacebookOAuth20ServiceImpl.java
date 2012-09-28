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
import org.scribe.oauth.OAuth20ServiceImpl;

/**
 * This class overload getAuthorizationUrl method to allow to add the Facebook state parameter to authorization URL
 * 
 * @author Mehdi BEN HAJ ABBES
 * @since 1.2.0
 */
public final class FacebookOAuth20ServiceImpl extends OAuth20ServiceImpl {
    
    private final DefaultApi20 api;
    
    private final OAuthConfig config;
    
    public FacebookOAuth20ServiceImpl(final DefaultApi20 api, final OAuthConfig config) {
        super(api, config);
        this.api = api;
        this.config = config;
    }
    
    public String getAuthorizationUrl(final String facebookState) {
        return ((ExtendedFacebookApi) this.api).getAuthorizationUrl(this.config, facebookState);
    }
}
