package org.pac4j.oauth.profile.strava;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * A Strava club.
 *
 * @author Adrian Papusoi
 * @since 1.7.0
 */
public class StravaClub implements Serializable {

    private static final long serialVersionUID = -1284645916528292643L;

    private String id;
    @JsonProperty("resource_state")
    private Integer resourceState;
    private String name;
    @JsonProperty("profile_medium")
    private String profileMedium;
    private String profile;

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
     * <p>Getter for the field <code>resourceState</code>.</p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public Integer getResourceState() {
        return resourceState;
    }

    /**
     * <p>Setter for the field <code>resourceState</code>.</p>
     *
     * @param resourceState a {@link java.lang.Integer} object
     */
    public void setResourceState(Integer resourceState) {
        this.resourceState = resourceState;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>profileMedium</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getProfileMedium() {
        return profileMedium;
    }

    /**
     * <p>Setter for the field <code>profileMedium</code>.</p>
     *
     * @param profileMedium a {@link java.lang.String} object
     */
    public void setProfileMedium(String profileMedium) {
        this.profileMedium = profileMedium;
    }

    /**
     * <p>Getter for the field <code>profile</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getProfile() {
        return profile;
    }

    /**
     * <p>Setter for the field <code>profile</code>.</p>
     *
     * @param profile a {@link java.lang.String} object
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }
}
