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

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

/**
 * Encapsulates a Club from a profile.
 * <br>
 * Example of a club:
 * {
 * "id": 37365,
 * "resource_state": 2,
 * "name": "Amicale Cycliste des Baltringues de Longchamp",
 * "profile_medium": "http:\/\/dgalywyr863hv.cloudfront.net\/pictures\/clubs\/37365\/1022943\/1\/medium.jpg",
 * "profile": "http:\/\/dgalywyr863hv.cloudfront.net\/pictures\/clubs\/37365\/1022943\/1\/large.jpg"
 * }
 *
 * @author Adrian Papusoi
 */
public class StravaClub extends JsonObject {

    private static final long serialVersionUID = -1284645916528292643L;

    private String id;
    private Integer resourceState;
    private String name;
    private String profileMedium;
    private String profile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getResourceState() {
        return resourceState;
    }

    public void setResourceState(Integer resourceState) {
        this.resourceState = resourceState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileMedium() {
        return profileMedium;
    }

    public void setProfileMedium(String profileMedium) {
        this.profileMedium = profileMedium;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    protected void buildFromJson(final JsonNode json) {
        this.id = (String) JsonHelper.convert(Converters.stringConverter, json, "id");
        this.resourceState = (Integer) JsonHelper.convert(Converters.integerConverter, json, "resource_state");
        this.name = (String) JsonHelper.convert(Converters.stringConverter, json, "name");
        this.profileMedium = (String) JsonHelper.convert(Converters.stringConverter, json, "profile_medium");
        this.profile = (String) JsonHelper.convert(Converters.stringConverter, json, "profile");
    }


}
