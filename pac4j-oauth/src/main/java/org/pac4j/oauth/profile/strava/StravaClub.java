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

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Integer getResourceState() {
        return resourceState;
    }

    public void setResourceState(final Integer resourceState) {
        this.resourceState = resourceState;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getProfileMedium() {
        return profileMedium;
    }

    public void setProfileMedium(final String profileMedium) {
        this.profileMedium = profileMedium;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(final String profile) {
        this.profile = profile;
    }
}
