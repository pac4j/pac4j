package org.pac4j.oauth.profile.foursquare;

import java.io.Serializable;

/**
 * This class represents a Foursquare user friend.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareUserFriend implements Serializable {
    
    private static final long serialVersionUID = 8954533489873703341L;

    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String relationship;
    private String photo;
    private String bio;
    private String location;
    private String email;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(final String gender) {
        this.gender = gender;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(final String relationship) {
        this.relationship = relationship;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(final String photo) {
        this.photo = photo;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(final String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
}
