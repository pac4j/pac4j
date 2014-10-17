/*
  Copyright 2012 - 2014 Jerome Leleu

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
package org.pac4j.oauth.profile.google2;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * This class defines the attributes of the Google profile (using OAuth 2.0 protocol).
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2AttributesDefinition extends OAuthAttributesDefinition {

    public static final String GENDER = "gender";
    public static final String DISPLAY_NAME = "displayName";
    public static final String GIVEN_NAME = "name.givenName";
    public static final String FAMILY_NAME = "name.familyName";
    public static final String URL = "url";
    public static final String PICTURE = "image.url";
    public static final String LANGUAGE = "language";
    public static final String BIRTHDAY = "birthday";
    public static final String EMAILS = "emails";

    public Google2AttributesDefinition() {
        addAttribute(GENDER, Converters.genderConverter);
        addAttribute(DISPLAY_NAME, Converters.stringConverter);
        addAttribute(GIVEN_NAME, Converters.stringConverter);
        addAttribute(FAMILY_NAME, Converters.stringConverter);
        addAttribute(URL, Converters.stringConverter);
        addAttribute(PICTURE, Converters.stringConverter);
        addAttribute(LANGUAGE, Converters.localeConverter);
        addAttribute(BIRTHDAY, Google2Converters.dateConverter);
        addAttribute(EMAILS, Google2Converters.listEmailConverter);
    }
}
