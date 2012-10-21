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

import org.scribe.up.profile.OAuthAttributesDefinition;
import org.scribe.up.profile.converter.Converters;

/**
 * This class defines the attributes of the Google profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GoogleAttributesDefinition extends OAuthAttributesDefinition {
    
    public static final String PROFILE_URL = "profileUrl";
    public static final String IS_VIEWER = "isViewer";
    public static final String THUMBNAIL_URL = "thumbnailUrl";
    public static final String FORMATTED = "formatted";
    public static final String FAMILY_NAME = "familyName";
    public static final String GIVEN_NAME = "givenName";
    public static final String DISPLAY_NAME = "displayName";
    public static final String URLS = "urls";
    public static final String PHOTOS = "photos";
    
    public GoogleAttributesDefinition() {
        addAttribute(PROFILE_URL, Converters.stringConverter);
        addAttribute(IS_VIEWER, Converters.booleanConverter);
        addAttribute(THUMBNAIL_URL, Converters.stringConverter);
        addAttribute(FORMATTED, Converters.stringConverter, false);
        addAttribute(FAMILY_NAME, Converters.stringConverter, false);
        addAttribute(GIVEN_NAME, Converters.stringConverter, false);
        addAttribute(DISPLAY_NAME, Converters.stringConverter);
        addAttribute(URLS, GoogleConverters.listObjectConverter);
        addAttribute(PHOTOS, GoogleConverters.listObjectConverter);
    }
}
