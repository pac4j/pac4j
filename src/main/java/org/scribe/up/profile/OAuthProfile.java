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
package org.scribe.up.profile;

import java.util.Map;

/**
 * This class is the minimal OAuth profile.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public abstract class OAuthProfile extends UserProfile {
    
    private static final long serialVersionUID = -2456133666711245607L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.oauthDefinition;
    }
    
    public OAuthProfile() {
        super();
    }
    
    public OAuthProfile(final Object id) {
        super(id);
    }
    
    public OAuthProfile(final Object id, final Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    /**
     * Set the access token
     * 
     * @param accessToken
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
        return (String) this.attributes.get(OAuthAttributesDefinition.ACCESS_TOKEN);
    }
}
