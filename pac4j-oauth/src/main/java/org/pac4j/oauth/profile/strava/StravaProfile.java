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

import java.util.Date;
import java.util.List;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;

/**
 * <p>Encapsulates a Strava athlete profile.</p>
 * 
 * Exemple of a json sent by Strava:
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
 * @since 1.7.0
 * @author Adrian Papusoi
 */
public class StravaProfile extends OAuth20Profile {

    private static final long serialVersionUID = 995023712830997358L;

    private static final String STRAVA_PROFILE_BASE_URL = "http://www.strava.com/athletes/";

    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.stravaDefinition;
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute("firstname");
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(StravaAttributesDefinition.LAST_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(StravaAttributesDefinition.FIRST_NAME);
    }

    @Override
    public String getEmail() {
        return (String) getAttribute(StravaAttributesDefinition.EMAIL);
    }

    @Override
    public String getPictureUrl() {
        return (String) getAttribute(StravaAttributesDefinition.PROFILE);
    }

    @Override
    public String getProfileUrl() {
        return STRAVA_PROFILE_BASE_URL + (String) getAttribute(StravaAttributesDefinition.ID);
    }

    @Override
    public String getLocation() {
        return (String) getAttribute(StravaAttributesDefinition.CITY);
    }

    @Override
    public Gender getGender() {
        String sex = (String) getAttribute(StravaAttributesDefinition.SEX);
        Gender result = Gender.UNSPECIFIED;
        if ("M".equals(sex)) {
            result = Gender.MALE;
        } else if ("F".equals(sex)) {
            result = Gender.FEMALE;
        }
        return result;
    }

    public Integer getResourceState() {
        return (Integer) getAttribute(StravaAttributesDefinition.RESOURCE_STATE);
    }

    public String getProfileMedium() {
        return (String) getAttribute(StravaAttributesDefinition.PROFILE_MEDIUM);
    }

    public String getState() {
        return (String) getAttribute(StravaAttributesDefinition.STATE);
    }

    public String getCountry() {
        return (String) getAttribute(StravaAttributesDefinition.COUNTRY);
    }

    public Boolean isPremium() {
        return (Boolean) getAttribute(StravaAttributesDefinition.PREMIUM);
    }

    public Date getCreatedAt() {
        return (Date) getAttribute(StravaAttributesDefinition.CREATED_AT);
    }

    public Date getUpdatedAt() {
        return (Date) getAttribute(StravaAttributesDefinition.UPDATED_AT);
    }

    public Integer getFollowerCount() {
        return (Integer) getAttribute(StravaAttributesDefinition.FOLLOWER_COUNT);
    }

    public Integer getFriendCount() {
        return (Integer) getAttribute(StravaAttributesDefinition.FRIEND_COUNT);
    }

    public String getDatePreference() {
        return (String) getAttribute(StravaAttributesDefinition.DATE_PREFERENCE);
    }

    public String getMeasurementPreference() {
        return (String) getAttribute(StravaAttributesDefinition.MEASUREMENT_PREFERENCE);
    }

    public List<StravaGear> getBikes() {
        return (List<StravaGear>) getAttribute(StravaAttributesDefinition.BIKES);
    }

    public List<StravaGear> getShoes() {
        return (List<StravaGear>) getAttribute(StravaAttributesDefinition.SHOES);
    }

    public List<StravaClub> getClubs() {
        return (List<StravaClub>) getAttribute(StravaAttributesDefinition.CLUBS);
    }

}
