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
package org.pac4j.oauth.profile;

import org.pac4j.core.profile.CommonProfile;

/**
 * This class is the base OAuth 2.0 profile, extending from the base {@link org.pac4j.core.profile.CommonProfile}. It deals with the OAuth
 * access token.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public abstract class OAuth20Profile extends CommonProfile {
    
    private static final long serialVersionUID = -2313972372691233648L;
    
    /**
     * Set the access token
     * 
     * @param accessToken the access token secret
     */
    public void setAccessToken(final String accessToken) {
        addAttribute(OAuthAttributesDefinition.ACCESS_TOKEN, accessToken);
    }
    
    /**
     * Return the access token.
     * 
     * @return the access token
     */
    public String getAccessToken() {
        return (String) getAttribute(OAuthAttributesDefinition.ACCESS_TOKEN);
    }
}
