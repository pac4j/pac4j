/*
  Copyright 2012 - 2015 pac4j organization

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
package org.pac4j.oauth.profile.foursquare;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * This class defines the attributes of the Foursquare profile.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareAttributesDefinition extends OAuthAttributesDefinition {

    public static final String ID = "id";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String GENDER = "gender";
    public static final String PHOTO = "photo";
    public static final String EMAIL = "email";
    public static final String FIRENDS = "friends";
    public static final String HOME_CITY = "homeCity";
    public static final String CONTACT = "contact";
    public static final String BIO = "bio";

    public FoursquareAttributesDefinition() {
        String[] names = new String[]{
                FIRST_NAME, LAST_NAME, GENDER, HOME_CITY, BIO, EMAIL, PHOTO
        };
        for (final String name : names) {
            addAttribute(name, Converters.stringConverter);
        }

        addAttribute(GENDER, Converters.genderConverter);
        addAttribute(FIRENDS, FoursquareConverters.friendsConverter);
        addAttribute(CONTACT, FoursquareConverters.contactConverter);
        addAttribute(PHOTO, FoursquareConverters.photoConverter);
    }
}
