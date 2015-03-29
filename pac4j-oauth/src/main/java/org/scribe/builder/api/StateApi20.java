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

/**
 * Implementation of the OAuth v2.0 protocol with state.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public abstract class StateApi20 extends DefaultApi20 {
    
    @Override
    public String getAuthorizationUrl(final OAuthConfig config) {
        throw new UnsupportedOperationException("Cannot invoke getAuthorizationUrl without state parameter");
    }
    
    /**
     * Returns the URL where you should redirect your users to authenticate your application.
     * 
     * @param config OAuth 2.0 configuration param object
     * @param state OAuth 2.0 state parameter
     * @return the URL where you should redirect your users
     */
    public abstract String getAuthorizationUrl(OAuthConfig config, String state);
}
