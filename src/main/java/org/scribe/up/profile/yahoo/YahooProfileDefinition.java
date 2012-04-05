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
package org.scribe.up.profile.yahoo;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.converter.Converters;
import org.scribe.up.profile.converter.DateConverter;
import org.scribe.up.profile.converter.FormattedDateConverter;
import org.scribe.up.profile.converter.GenderConverter;
import org.scribe.up.profile.converter.JsonListConverter;
import org.scribe.up.profile.converter.JsonObjectConverter;

/**
 * This class defines the attributes of the Yahoo profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class YahooProfileDefinition extends AttributesDefinition {
    
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
    
    public YahooProfileDefinition() {
        attributes.add(ABOUT_ME);
        converters.put(ABOUT_ME, Converters.stringConverter);
        attributes.add(ADDRESSES);
        converters.put(ADDRESSES, new JsonListConverter(YahooAddress.class));
        attributes.add(BIRTH_YEAR);
        converters.put(BIRTH_YEAR, Converters.integerConverter);
        attributes.add(BIRTHDATE);
        converters.put(BIRTHDATE, new FormattedDateConverter("MM/dd"));
        DateConverter dateConverter = new FormattedDateConverter("yyyy-MM-dd'T'HH:mm:ss'Z'");
        attributes.add(CREATED);
        converters.put(CREATED, dateConverter);
        attributes.add(DISPLAY_AGE);
        converters.put(DISPLAY_AGE, Converters.integerConverter);
        attributes.add(DISCLOSURES);
        converters.put(DISCLOSURES, new JsonListConverter(YahooDisclosure.class));
        attributes.add(EMAILS);
        converters.put(EMAILS, new JsonListConverter(YahooEmail.class));
        attributes.add(FAMILY_NAME);
        converters.put(FAMILY_NAME, Converters.stringConverter);
        attributes.add(GENDER);
        converters.put(GENDER, new GenderConverter("m", "f"));
        attributes.add(GIVEN_NAME);
        converters.put(GIVEN_NAME, Converters.stringConverter);
        attributes.add(IMAGE);
        converters.put(IMAGE, new JsonObjectConverter(YahooImage.class));
        attributes.add(INTERESTS);
        converters.put(INTERESTS, new JsonListConverter(YahooInterest.class));
        attributes.add(IS_CONNECTED);
        converters.put(IS_CONNECTED, Converters.booleanConverter);
        attributes.add(LANG);
        converters.put(LANG, Converters.localeConverter);
        attributes.add(LOCATION);
        converters.put(LOCATION, Converters.stringConverter);
        attributes.add(MEMBER_SINCE);
        converters.put(MEMBER_SINCE, dateConverter);
        attributes.add(NICKNAME);
        converters.put(NICKNAME, Converters.stringConverter);
        attributes.add(PROFILE_URL);
        converters.put(PROFILE_URL, Converters.stringConverter);
        attributes.add(TIME_ZONE);
        converters.put(TIME_ZONE, Converters.stringConverter);
        attributes.add(UPDATED);
        converters.put(UPDATED, dateConverter);
        attributes.add(URI);
        converters.put(URI, Converters.stringConverter);
    }
}
