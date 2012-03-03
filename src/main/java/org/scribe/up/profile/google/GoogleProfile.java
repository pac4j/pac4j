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

import java.util.List;

import org.scribe.up.profile.UserProfile;
import org.scribe.up.util.ObjectHelper;

/**
 * This class is the user profile for Google with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class GoogleProfile extends UserProfile {
    
    public static final String ENTRY = "entry";
    public static final String ID = "id";
    public static final String PROFILE_URL = "profileUrl";
    public static final String IS_VIEWER = "isViewer";
    public static final String THUMBNAIL_URL = "thumbnailUrl";
    public static final String NAME = "name";
    public static final String FORMATTED = "formatted";
    public static final String FAMILY_NAME = "familyName";
    public static final String GIVEN_NAME = "givenName";
    public static final String DISPLAY_NAME = "displayName";
    public static final String URLS = "urls";
    public static final String PHOTOS = "photos";
    
    public String getProfileUrl() {
        return (String) attributes.get(PROFILE_URL);
    }
    
    public boolean isViewer() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(IS_VIEWER), Boolean.FALSE);
    }
    
    public String getThumbnailUrl() {
        return (String) attributes.get(THUMBNAIL_URL);
    }
    
    public String getFormatted() {
        return (String) attributes.get(FORMATTED);
    }
    
    public String getFamilyName() {
        return (String) attributes.get(FAMILY_NAME);
    }
    
    public String getGivenName() {
        return (String) attributes.get(GIVEN_NAME);
    }
    
    public String getDisplayName() {
        return (String) attributes.get(DISPLAY_NAME);
    }
    
    public List<GoogleObject> getUrls() {
        return (List<GoogleObject>) attributes.get(URLS);
    }
    
    public List<GoogleObject> getPhotos() {
        return (List<GoogleObject>) attributes.get(PHOTOS);
    }
}
