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
    
    public static final String EMAIL = "email";
    public static final String VERIFIED_EMAIL = "verified_email";
    public static final String NAME = "name";
    public static final String GIVEN_NAME = "given_name";
    public static final String FAMILY_NAME = "family_name";
    public static final String LINK = "link";
    public static final String PICTURE = "picture";
    public static final String GENDER = "gender";
    public static final String LOCALE = "locale";
    public static final String BIRTHDAY = "birthday";
    
    public Google2AttributesDefinition() {
        addAttribute(EMAIL, Converters.stringConverter);
        addAttribute(VERIFIED_EMAIL, Converters.booleanConverter);
        addAttribute(NAME, Converters.stringConverter);
        addAttribute(GIVEN_NAME, Converters.stringConverter);
        addAttribute(FAMILY_NAME, Converters.stringConverter);
        addAttribute(LINK, Converters.stringConverter);
        addAttribute(PICTURE, Converters.stringConverter);
        addAttribute(GENDER, Converters.genderConverter);
        addAttribute(LOCALE, Converters.localeConverter);
        addAttribute(BIRTHDAY, Google2Converters.dateConverter);
    }
}
