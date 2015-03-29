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

/**
 * This class is the base OAuth 1.0 profile, extending from the base {@link org.pac4j.oauth.profile.OAuth20Profile}. It deals with the OAuth
 * access token secret.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public abstract class OAuth10Profile extends OAuth20Profile {
    
    private static final long serialVersionUID = 3407397824720340476L;
    
    /**
     * Set the access token secret
     * 
     * @param accessSecret the access token secret
     */
    public void setAccessSecret(final String accessSecret) {
        addAttribute(OAuthAttributesDefinition.ACCESS_SECRET, accessSecret);
    }
    
    /**
     * Return the access token secret.
     * 
     * @return the access token secret
     */
    public String getAccessSecret() {
        return (String) getAttribute(OAuthAttributesDefinition.ACCESS_SECRET);
    }
}
