package org.pac4j.oauth.profile.foursquare;

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonObject;

public class FoursquareUserFriend extends JsonObject{
    
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
