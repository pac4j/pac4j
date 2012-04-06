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
package org.scribe.up.profile.linkedin;

import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.ProfileDefinitions;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.impl.LinkedInProvider;

/**
 * This class is the user profile for LinkedIn with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class LinkedInProfile extends UserProfile {
    
    private static final long serialVersionUID = 6502082351173525371L;
    
    protected AttributesDefinition getAttributesDefinition() {
        return ProfileDefinitions.linkedinDefinition;
    }
    
    protected String getProviderType() {
        return LinkedInProvider.TYPE;
    }
    
    public LinkedInProfile() {
        super();
    }
    
    public LinkedInProfile(Object id) {
        super(id);
    }
    
    public LinkedInProfile(Object id, Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getFirstName() {
        return (String) attributes.get(LinkedInProfileDefinition.FIRST_NAME);
    }
    
    public String getLastName() {
        return (String) attributes.get(LinkedInProfileDefinition.LAST_NAME);
    }
    
    public String getHeadline() {
        return (String) attributes.get(LinkedInProfileDefinition.HEADLINE);
    }
    
    public String getUrl() {
        return (String) attributes.get(LinkedInProfileDefinition.URL);
    }
}
