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
package org.pac4j.oauth.profile.facebook;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.FormattedDateConverter;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.converter.JsonListConverter;
import org.pac4j.oauth.profile.facebook.converter.FacebookRelationshipStatusConverter;

import java.util.Arrays;

/**
 * This class defines the attributes of the Facebook profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class FacebookAttributesDefinition extends AttributesDefinition {
    
    public static final String NAME = "name";
    public static final String FIRST_NAME = "first_name";
    public static final String MIDDLE_NAME = "middle_name";
    public static final String LAST_NAME = "last_name";
    public static final String GENDER = "gender";
    public static final String LOCALE = "locale";
    public static final String LANGUAGES = "languages";
    public static final String LINK = "link";
    public static final String THIRD_PARTY_ID = "third_party_id";
    public static final String TIMEZONE = "timezone";
    public static final String UPDATED_TIME = "updated_time";
    public static final String VERIFIED = "verified";
    public static final String BIO = "bio";
    public static final String BIRTHDAY = "birthday";
    public static final String EDUCATION = "education";
    public static final String EMAIL = "email";
    public static final String HOMETOWN = "hometown";
    public static final String INTERESTED_IN = "interested_in";
    public static final String LOCATION = "location";
    public static final String POLITICAL = "political";
    public static final String FAVORITE_ATHLETES = "favorite_athletes";
    public static final String FAVORITE_TEAMS = "favorite_teams";
    public static final String QUOTES = "quotes";
    public static final String RELATIONSHIP_STATUS = "relationship_status";
    public static final String RELIGION = "religion";
    public static final String SIGNIFICANT_OTHER = "significant_other";
    public static final String WEBSITE = "website";
    public static final String WORK = "work";
    public static final String FRIENDS = "friends";
    public static final String MOVIES = "movies";
    public static final String MUSIC = "music";
    public static final String BOOKS = "books";
    public static final String LIKES = "likes";
    public static final String ALBUMS = "albums";
    public static final String EVENTS = "events";
    public static final String GROUPS = "groups";
    public static final String MUSIC_LISTENS = "music.listens";
    public static final String PICTURE = "picture";
    
    public FacebookAttributesDefinition() {
        Arrays.stream(new String[] {
            NAME, FIRST_NAME, MIDDLE_NAME, LAST_NAME, LINK, THIRD_PARTY_ID, BIO, EMAIL, POLITICAL, QUOTES,
            RELIGION, WEBSITE
        }).forEach(name -> primary(name));
        primary(TIMEZONE, Converters.integerConverter);
        primary(VERIFIED, Converters.booleanConverter);
        final JsonConverter<FacebookObject> objectConverter = new JsonConverter<>(FacebookObject.class);
        final JsonListConverter multiObjectConverter = new JsonListConverter(FacebookObject.class, FacebookObject[].class);
        final JsonListConverter multiStringConverter = new JsonListConverter(String.class, String[].class);
        final JsonListConverter multiInfoConverter = new JsonListConverter(FacebookInfo.class, FacebookInfo[].class);
        primary(GENDER, Converters.genderConverter);
        primary(LOCALE, Converters.localeConverter);
        primary(UPDATED_TIME, Converters.dateConverter);
        primary(BIRTHDAY, new FormattedDateConverter("MM/dd/yyyy"));
        primary(RELATIONSHIP_STATUS, new FacebookRelationshipStatusConverter());
        primary(LANGUAGES, multiObjectConverter);
        primary(EDUCATION, new JsonListConverter(FacebookEducation.class, FacebookEducation[].class));
        primary(HOMETOWN, objectConverter);
        primary(INTERESTED_IN, multiStringConverter);
        primary(LOCATION, objectConverter);
        primary(FAVORITE_ATHLETES, multiObjectConverter);
        primary(FAVORITE_TEAMS, multiObjectConverter);
        primary(SIGNIFICANT_OTHER, objectConverter);
        primary(WORK, new JsonListConverter(FacebookWork.class, FacebookWork[].class));
        secondary(FRIENDS, multiObjectConverter);
        secondary(MOVIES, multiInfoConverter);
        secondary(MUSIC, multiInfoConverter);
        secondary(BOOKS, multiInfoConverter);
        secondary(LIKES, multiInfoConverter);
        secondary(ALBUMS, new JsonListConverter(FacebookPhoto.class, FacebookPhoto[].class));
        secondary(EVENTS, new JsonListConverter(FacebookEvent.class, FacebookEvent[].class));
        secondary(GROUPS, new JsonListConverter(FacebookGroup.class, FacebookGroup[].class));
        secondary(MUSIC_LISTENS, new JsonListConverter(FacebookMusicListen.class, FacebookMusicListen[].class));
        secondary(PICTURE, new JsonConverter<>(FacebookPicture.class));
    }
}
