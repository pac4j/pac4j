package org.pac4j.oauth.profile.facebook;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import static org.pac4j.core.util.CommonHelper.newDate;

/**
 * This class represents a Facebook work.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookWork implements Serializable {

    private static final long serialVersionUID = -5698634125512204910L;

    private FacebookObject employer;

    private FacebookObject location;

    private FacebookObject position;

    private String description;

    @JsonProperty("start_date")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM")
    private Date startDate;

    @JsonProperty("end_date")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM")
    private Date endDate;

    /**
     * <p>Getter for the field <code>employer</code>.</p>
     *
     * @return a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public FacebookObject getEmployer() {
        return employer;
    }

    /**
     * <p>Setter for the field <code>employer</code>.</p>
     *
     * @param employer a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public void setEmployer(FacebookObject employer) {
        this.employer = employer;
    }

    /**
     * <p>Getter for the field <code>location</code>.</p>
     *
     * @return a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public FacebookObject getLocation() {
        return location;
    }

    /**
     * <p>Setter for the field <code>location</code>.</p>
     *
     * @param location a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public void setLocation(FacebookObject location) {
        this.location = location;
    }

    /**
     * <p>Getter for the field <code>position</code>.</p>
     *
     * @return a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public FacebookObject getPosition() {
        return position;
    }

    /**
     * <p>Setter for the field <code>position</code>.</p>
     *
     * @param position a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public void setPosition(FacebookObject position) {
        this.position = position;
    }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>startDate</code>.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getStartDate() {
        return newDate(startDate);
    }

    /**
     * <p>Setter for the field <code>startDate</code>.</p>
     *
     * @param startDate a {@link java.util.Date} object
     */
    public void setStartDate(Date startDate) {
        this.startDate = newDate(startDate);
    }

    /**
     * <p>Getter for the field <code>endDate</code>.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getEndDate() {
        return newDate(endDate);
    }

    /**
     * <p>Setter for the field <code>endDate</code>.</p>
     *
     * @param endDate a {@link java.util.Date} object
     */
    public void setEndDate(Date endDate) {
        this.endDate = newDate(endDate);
    }
}
