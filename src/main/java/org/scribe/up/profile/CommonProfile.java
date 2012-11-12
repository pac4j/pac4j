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
 * This interface gathers the default attribute getters which can be retrieved for most of the profiles.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public interface CommonProfile {
    
    /**
     * Return the email of the user.
     * 
     * @return the email of the user
     */
    public String getEmail();
    
    /**
     * Return the first name of the user.
     * 
     * @return the first name of the user
     */
    public String getFirstName();
    
    /**
     * Return the family name of the user.
     * 
     * @return the family name of the user
     */
    public String getFamilyName();
    
    /**
     * Return the displayed name of the user. It can be the username or the first and last names (separated by a space).
     * 
     * @return the displayed name of the user
     */
    public String getDisplayName();
    
    /**
     * Return the username of the user. It can be a login or a specific username.
     * 
     * @return the username of the user
     */
    public String getUsername();
    
    /**
     * Return the gender of the user.
     * 
     * @return the gender of the user
     */
    public Gender getGender();
    
    /**
     * Return the locale of the user.
     * 
     * @return the locale of the user
     */
    public Locale getLocale();
    
    /**
     * Return the url of the picture of the user.
     * 
     * @return the url of the picture of the user.
     */
    public String getPictureUrl();
    
    /**
     * Return the url of the profile of the user.
     * 
     * @return the url of the profile of the user.
     */
    public String getProfileUrl();
    
    /**
     * Return the location of the user.
     * 
     * @return the locationle of the user
     */
    public String getLocation();
}
