package org.pac4j.oauth.profile.strava;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pac4j.oauth.profile.JsonObject;

/**
 * A Strava gear.
 *
 * @author Adrian Papusoi
 * @since 1.7.0
 */
public class StravaGear extends JsonObject {

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

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getResourceState() {
        return resourceState;
    }

    public void setResourceState(Integer resourceState) {
        this.resourceState = resourceState;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }
}
