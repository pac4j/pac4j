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

import java.util.Locale;

/**
 * This class is the default implementation of the {@link CommonProfile} interface.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */

public abstract class DefaultCommonProfile extends UserProfile implements CommonProfile {
    
    private static final long serialVersionUID = 7233145076116494257L;
    
    public String getEmail() {
        return (String) get("email");
    }
    
    public String getFirstName() {
        return (String) get("first_name");
    }
    
    public String getFamilyName() {
        return (String) get("family_name");
    }
    
    public String getDisplayName() {
        return (String) get("display_name");
    }
    
    public String getUsername() {
        return (String) get("username");
    }
    
    public Gender getGender() {
        return (Gender) get("gender");
    }
    
    public Locale getLocale() {
        return (Locale) get("locale");
    }
    
    public String getPictureUrl() {
        return (String) get("picture_url");
    }
    
    public String getProfileUrl() {
        return (String) get("profile_url");
    }
    
    public String getLocation() {
        return (String) get("location");
    }
}
