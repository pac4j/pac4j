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
package org.pac4j.oauth.profile.yahoo;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * This class defines the attributes of the Yahoo profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class YahooAttributesDefinition extends OAuthAttributesDefinition {
    
    public static final String ABOUT_ME = "aboutMe";
    public static final String ADDRESSES = "addresses";
    public static final String BIRTH_YEAR = "birthYear";
    public static final String BIRTHDATE = "birthdate";
    public static final String CREATED = "created";
    public static final String DISPLAY_AGE = "displayAge";
    public static final String DISCLOSURES = "disclosures";
    public static final String EMAILS = "emails";
    public static final String FAMILY_NAME = "familyName";
    public static final String GENDER = "gender";
    public static final String GIVEN_NAME = "givenName";
    public static final String IMAGE = "image";
    public static final String INTERESTS = "interests";
    public static final String IS_CONNECTED = "isConnected";
    public static final String LANG = "lang";
    public static final String LOCATION = "location";
    public static final String MEMBER_SINCE = "memberSince";
    public static final String NICKNAME = "nickname";
    public static final String PROFILE_URL = "profileUrl";
    public static final String TIME_ZONE = "timeZone";
    public static final String UPDATED = "updated";
    public static final String URI = "uri";
    
    public YahooAttributesDefinition() {
        primary(ABOUT_ME, Converters.stringConverter);
        primary(ADDRESSES, YahooConverters.listAddressConverter);
        primary(BIRTH_YEAR, Converters.integerConverter);
        primary(BIRTHDATE, YahooConverters.birthdateConverter);
        primary(CREATED, YahooConverters.dateConverter);
        primary(DISPLAY_AGE, Converters.integerConverter);
        primary(DISCLOSURES, YahooConverters.listDisclosureConverter);
        primary(EMAILS, YahooConverters.listEmailConverter);
        primary(FAMILY_NAME, Converters.stringConverter);
        primary(GENDER, YahooConverters.genderConverter);
        primary(GIVEN_NAME, Converters.stringConverter);
        primary(IMAGE, YahooConverters.imageConverter);
        primary(INTERESTS, YahooConverters.listInterestConverter);
        primary(IS_CONNECTED, Converters.booleanConverter);
        primary(LANG, Converters.localeConverter);
        primary(LOCATION, Converters.stringConverter);
        primary(MEMBER_SINCE, YahooConverters.dateConverter);
        primary(NICKNAME, Converters.stringConverter);
        primary(PROFILE_URL, Converters.stringConverter);
        primary(TIME_ZONE, Converters.stringConverter);
        primary(UPDATED, YahooConverters.dateConverter);
        primary(URI, Converters.stringConverter);
    }
}
