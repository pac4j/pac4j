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
package org.scribe.up.profile.windowslive;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.UserProfile;

/**
 * This class is the user profile for Windows Live with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WindowsLiveProfile extends UserProfile {
    
    private static final long serialVersionUID = 4835657443547427620L;
    
    protected AttributesDefinition getAttributesDefinition() {
        return AttributesDefinitions.windowsLiveDefinition;
    }
    
    public WindowsLiveProfile() {
        super();
    }
    
    public WindowsLiveProfile(Object id) {
        super(id);
    }
    
    public WindowsLiveProfile(Object id, Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getName() {
        return (String) attributes.get(WindowsLiveAttributesDefinition.NAME);
    }
    
    public String getFirstName() {
        return (String) attributes.get(WindowsLiveAttributesDefinition.FIRST_NAME);
    }
    
    public String getLastName() {
        return (String) attributes.get(WindowsLiveAttributesDefinition.LAST_NAME);
    }
    
    public String getLink() {
        return (String) attributes.get(WindowsLiveAttributesDefinition.LINK);
    }
    
    public Gender getGender() {
        return (Gender) attributes.get(WindowsLiveAttributesDefinition.GENDER);
    }
    
    public Locale getLocale() {
        return (Locale) attributes.get(WindowsLiveAttributesDefinition.LOCALE);
    }
    
    public Date getUpdatedTime() {
        return (Date) attributes.get(WindowsLiveAttributesDefinition.UPDATED_TIME);
    }
}
