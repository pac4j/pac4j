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

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonObject;

/**
 * This class represents a Foursquare user friend.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareUserFriend extends JsonObject{
    
    private static final long serialVersionUID = 8954533489873703341L;

    private String id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String relationship;
    private String photo;
    private String bio;
    private String location;
    private String email;
    
    @Override
    protected void buildFromJson(JsonNode json) {
         id = json.get("id").asText();
         firstName = json.get("firstName").asText();
         lastName = json.get("lastName").asText();
         gender = Converters.genderConverter.convert(json.get("gender").asText());
         relationship = json.get("relationship").asText();
         photo = json.get("photo").asText();
         bio = json.get("bio").asText();
         location = json.get("location").asText();
         email = json.get("email").asText();
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public String getRelationship() {
        return relationship;
    }

    public String getPhoto() {
        return photo;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getEmail() {
        return email;
    }
}
