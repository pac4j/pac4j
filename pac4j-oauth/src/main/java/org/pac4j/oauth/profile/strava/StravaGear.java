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
     * <p>Getter for the field <code>primary</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean getPrimary() {
        return primary;
    }

    /**
     * <p>Setter for the field <code>primary</code>.</p>
     *
     * @param primary a {@link java.lang.Boolean} object
     */
    public void setPrimary(Boolean primary) {
        this.primary = primary;
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
     * <p>Getter for the field <code>distance</code>.</p>
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getDistance() {
        return distance;
    }

    /**
     * <p>Setter for the field <code>distance</code>.</p>
     *
     * @param distance a {@link java.lang.Long} object
     */
    public void setDistance(Long distance) {
        this.distance = distance;
    }
}
