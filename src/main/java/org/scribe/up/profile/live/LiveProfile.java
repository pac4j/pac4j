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
package org.scribe.up.profile.live;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.impl.LiveProvider;

/**
 * This class is the user profile for Windows Live with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class LiveProfile extends UserProfile {
    
    private static final long serialVersionUID = 4957023145113094752L;
    
    protected AttributesDefinition getAttributesDefinition() {
        return AttributesDefinitions.liveDefinition;
    }
    
    protected String getProviderType() {
        return LiveProvider.TYPE;
    }
    
    public LiveProfile() {
        super();
    }
    
    public LiveProfile(Object id) {
        super(id);
    }
    
    public LiveProfile(Object id, Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    /**
     * Indicate if the user identifier matches this kind of profile.
     * 
     * @param id
     * @return if the user identifier matches this kind of profile
     */
    public static boolean isTypedIdOf(String id) {
        if (id != null && id.startsWith(LiveProvider.TYPE + SEPARATOR)) {
            return true;
        } else {
            return false;
        }
    }
    
    public String getName() {
        return (String) attributes.get(LiveAttributesDefinition.NAME);
    }
    
    public String getFirstName() {
        return (String) attributes.get(LiveAttributesDefinition.FIRST_NAME);
    }
    
    public String getLastName() {
        return (String) attributes.get(LiveAttributesDefinition.LAST_NAME);
    }
    
    public String getLink() {
        return (String) attributes.get(LiveAttributesDefinition.LINK);
    }
    
    public Gender getGender() {
        return (Gender) attributes.get(LiveAttributesDefinition.GENDER);
    }
    
    public Locale getLocale() {
        return (Locale) attributes.get(LiveAttributesDefinition.LOCALE);
    }
    
    public Date getUpdatedTime() {
        return (Date) attributes.get(LiveAttributesDefinition.UPDATED_TIME);
    }
}
