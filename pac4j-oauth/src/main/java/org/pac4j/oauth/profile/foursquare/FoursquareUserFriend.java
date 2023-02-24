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

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.lang.String} object
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>firstName</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * <p>Setter for the field <code>firstName</code>.</p>
     *
     * @param firstName a {@link java.lang.String} object
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * <p>Getter for the field <code>lastName</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * <p>Setter for the field <code>lastName</code>.</p>
     *
     * @param lastName a {@link java.lang.String} object
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * <p>Getter for the field <code>gender</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getGender() {
        return gender;
    }

    /**
     * <p>Setter for the field <code>gender</code>.</p>
     *
     * @param gender a {@link java.lang.String} object
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * <p>Getter for the field <code>relationship</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getRelationship() {
        return relationship;
    }

    /**
     * <p>Setter for the field <code>relationship</code>.</p>
     *
     * @param relationship a {@link java.lang.String} object
     */
    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    /**
     * <p>Getter for the field <code>photo</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * <p>Setter for the field <code>photo</code>.</p>
     *
     * @param photo a {@link java.lang.String} object
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * <p>Getter for the field <code>bio</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getBio() {
        return bio;
    }

    /**
     * <p>Setter for the field <code>bio</code>.</p>
     *
     * @param bio a {@link java.lang.String} object
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * <p>Getter for the field <code>location</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getLocation() {
        return location;
    }

    /**
     * <p>Setter for the field <code>location</code>.</p>
     *
     * @param location a {@link java.lang.String} object
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * <p>Getter for the field <code>email</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getEmail() {
        return email;
    }

    /**
     * <p>Setter for the field <code>email</code>.</p>
     *
     * @param email a {@link java.lang.String} object
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
