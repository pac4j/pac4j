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

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.converter.Converters;
import org.scribe.up.profile.converter.JsonListConverter;

/**
 * This class defines the attributes of the Google profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GoogleProfileDefinition extends AttributesDefinition {
    
    public static final String PROFILE_URL = "profileUrl";
    public static final String IS_VIEWER = "isViewer";
    public static final String THUMBNAIL_URL = "thumbnailUrl";
    public static final String FORMATTED = "formatted";
    public static final String FAMILY_NAME = "familyName";
    public static final String GIVEN_NAME = "givenName";
    public static final String DISPLAY_NAME = "displayName";
    public static final String URLS = "urls";
    public static final String PHOTOS = "photos";
    
    public GoogleProfileDefinition() {
        attributes.add(PROFILE_URL);
        converters.put(PROFILE_URL, Converters.stringConverter);
        attributes.add(IS_VIEWER);
        converters.put(IS_VIEWER, Converters.booleanConverter);
        attributes.add(THUMBNAIL_URL);
        converters.put(THUMBNAIL_URL, Converters.stringConverter);
        attributes.add(FORMATTED);
        converters.put(FORMATTED, Converters.stringConverter);
        attributes.add(FAMILY_NAME);
        converters.put(FAMILY_NAME, Converters.stringConverter);
        attributes.add(GIVEN_NAME);
        converters.put(GIVEN_NAME, Converters.stringConverter);
        attributes.add(DISPLAY_NAME);
        converters.put(DISPLAY_NAME, Converters.stringConverter);
        JsonListConverter listGoogleObjectConverter = new JsonListConverter(GoogleObject.class);
        attributes.add(URLS);
        converters.put(URLS, listGoogleObjectConverter);
        attributes.add(PHOTOS);
        converters.put(PHOTOS, listGoogleObjectConverter);
    }
}
