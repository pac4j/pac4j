package org.pac4j.oauth.profile.strava;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * A Strava gear.
 *
 * @author Adrian Papusoi
 * @since 1.7.0
 */
public class StravaGear implements Serializable {

    private static final long serialVersionUID = -5738356602119292294L;

    private String id;
    private Boolean primary;
    private String name;
    @JsonProperty("resource_state")
    private Integer resourceState;
    private Long distance;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(final Boolean primary) {
        this.primary = primary;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getResourceState() {
        return resourceState;
    }

    public void setResourceState(final Integer resourceState) {
        this.resourceState = resourceState;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(final Long distance) {
        this.distance = distance;
    }
}
