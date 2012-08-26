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
package org.scribe.up.profile.google;

import java.util.Locale;
import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.UserProfile;

/**
 * This class is the user profile for Google (using OAuth protocol version 2) with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2Profile extends UserProfile {
    
    private static final long serialVersionUID = 2308680174911185443L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return AttributesDefinitions.google2Definition;
    }
    
    public Google2Profile() {
        super();
    }
    
    public Google2Profile(final Object id) {
        super(id);
    }
    
    public Google2Profile(final Object id, final Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getEmail() {
        return (String) attributes.get(Google2AttributesDefinition.EMAIL);
    }
    
    public boolean isVerifiedEmail() {
        return getSafeBoolean((Boolean) attributes.get(Google2AttributesDefinition.VERIFIED_EMAIL));
    }
    
    /**
     * Indicate if the verified_email attribute exists.
     * 
     * @return if the verified_email attribute exists
     */
    public boolean isVerifiedEmailDefined() {
        return attributes.get(Google2AttributesDefinition.VERIFIED_EMAIL) != null;
    }
    
    public String getName() {
        return (String) attributes.get(Google2AttributesDefinition.NAME);
    }
    
    public String getGivenName() {
        return (String) attributes.get(Google2AttributesDefinition.GIVEN_NAME);
    }
    
    public String getFamilyName() {
        return (String) attributes.get(Google2AttributesDefinition.FAMILY_NAME);
    }
    
    public String getLink() {
        return (String) attributes.get(Google2AttributesDefinition.LINK);
    }
    
    public String getPicture() {
        return (String) attributes.get(Google2AttributesDefinition.PICTURE);
    }
    
    public Gender getGender() {
        return (Gender) attributes.get(Google2AttributesDefinition.GENDER);
    }
    
    public Locale getLocale() {
        return (Locale) attributes.get(Google2AttributesDefinition.LOCALE);
    }
}
