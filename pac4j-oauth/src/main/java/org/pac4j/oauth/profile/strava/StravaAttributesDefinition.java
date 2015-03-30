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
package org.pac4j.oauth.profile.strava;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * Example of Strava profile:
 * {
 * "id": 1321007,
 * "resource_state": 3,
 * "firstname": "Adrian",
 * "lastname": "Papusoi",
 * "profile_medium": "http:\/\/dgalywyr863hv.cloudfront.net\/pictures\/athletes\/1321007\/361713\/1\/medium.jpg",
 * "profile": "http:\/\/dgalywyr863hv.cloudfront.net\/pictures\/athletes\/1321007\/361713\/1\/large.jpg",
 * "city": "Courbevoie",
 * "state": "\u00cele-de-France",
 * "country": "France",
 * "sex": "M",
 * "friend": null,
 * "follower": null,
 * "premium": false,
 * "created_at": "2012-11-13T10:13:38Z",
 * "updated_at": "2014-12-17T13:30:39Z",
 * "badge_type_id": 0,
 * "follower_count": 24,
 * "friend_count": 30,
 * "mutual_friend_count": 0,
 * "date_preference": "%d\/%m\/%Y",
 * "measurement_preference": "meters",
 * "email": "adrian.papusoi@gmail.com",
 * "ftp": null,
 * "clubs": [
 * {
 * "id": 37365,
 * "resource_state": 2,
 * "name": "Amicale Cycliste des Baltringues de Longchamp",
 * "profile_medium": "http:\/\/dgalywyr863hv.cloudfront.net\/pictures\/clubs\/37365\/1022943\/1\/medium.jpg",
 * "profile": "http:\/\/dgalywyr863hv.cloudfront.net\/pictures\/clubs\/37365\/1022943\/1\/large.jpg"
 * },
 * {
 * "id": 45060,
 * "resource_state": 2,
 * "name": "VeloViewer",
 * "profile_medium": "http:\/\/dgalywyr863hv.cloudfront.net\/pictures\/clubs\/45060\/1141016\/3\/medium.jpg",
 * "profile": "http:\/\/dgalywyr863hv.cloudfront.net\/pictures\/clubs\/45060\/1141016\/3\/large.jpg"
 * },
 * {
 * "id": 21017,
 * "resource_state": 2,
 * "name": "Paris cycling meetup group",
 * "profile_medium": "http:\/\/dgalywyr863hv.cloudfront.net\/pictures\/clubs\/21017\/449495\/1\/medium.jpg",
 * "profile": "http:\/\/dgalywyr863hv.cloudfront.net\/pictures\/clubs\/21017\/449495\/1\/large.jpg"
 * }
 * ],
 * "bikes": [
 * {
 * "id": "b1232920",
 * "primary": false,
 * "name": "BH G5",
 * "resource_state": 2,
 * "distance": 4587858
 * },
 * {
 * "id": "b662369",
 * "primary": false,
 * "name": "Gitane '80s",
 * "resource_state": 2,
 * "distance": 371587
 * },
 * {
 * "id": "b526413",
 * "primary": false,
 * "name": "Grand Canyon AL 6.0",
 * "resource_state": 2,
 * "distance": 362774
 * },
 * {
 * "id": "b1534132",
 * "primary": true,
 * "name": "Kona Dr Good",
 * "resource_state": 2,
 * "distance": 1303618
 * }
 * ],
 * "shoes": [
 * ]
 * }
 *
 * @author Adrian Papusoi
 */
public class StravaAttributesDefinition extends OAuthAttributesDefinition {

    public static final String ID = "id";
    public static final String RESOURCE_STATE = "resource_state";
    public static final String FIRST_NAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String PROFILE_MEDIUM = "profile_medium";
    public static final String PROFILE = "profile";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String COUNTRY = "country";
    public static final String SEX = "sex";
    // friend
    // follower
    public static final String PREMIUM = "premium";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String BADGE_TYPE_ID = "badge_type_id";
    public static final String FOLLOWER_COUNT = "follower_count";
    public static final String FRIEND_COUNT = "friend_count";
    // mutual_friend_count
    public static final String DATE_PREFERENCE = "date_preference";
    public static final String MEASUREMENT_PREFERENCE = "measurement_preference";
    public static final String EMAIL = "email";
    // ftp
    public static final String CLUBS = "clubs";
    public static final String BIKES = "bikes";
    public static final String SHOES = "shoes";

    public StravaAttributesDefinition() {
        final String[] names = new String[] { FIRST_NAME, LAST_NAME, PROFILE_MEDIUM, PROFILE, CITY, STATE, COUNTRY,
                SEX, DATE_PREFERENCE, MEASUREMENT_PREFERENCE, EMAIL };
        for (final String name : names) {
            addAttribute(name, Converters.stringConverter);
        }
        addAttribute(ID, Converters.longConverter);
        addAttribute(RESOURCE_STATE, Converters.integerConverter);
        addAttribute(PREMIUM, Converters.booleanConverter);
        addAttribute(CREATED_AT, StravaConverters.dateConverter);
        addAttribute(UPDATED_AT, StravaConverters.dateConverter);
        addAttribute(BADGE_TYPE_ID, Converters.integerConverter);
        addAttribute(FOLLOWER_COUNT, Converters.integerConverter);
        addAttribute(FRIEND_COUNT, Converters.integerConverter);

        addAttribute(CLUBS, StravaConverters.clubListConverter);
        addAttribute(BIKES, StravaConverters.gearListConverter);
        addAttribute(SHOES, StravaConverters.gearListConverter);
    }
}
