package org.pac4j.oauth.profile.facebook;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import static org.pac4j.core.util.CommonHelper.newDate;

/**
 * This class represents a Facebook event.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class FacebookEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1790651609769453424L;

    private String id;

    private String name;

    @JsonProperty("start_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss")
    private Date startTime;

    @JsonProperty("end_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss")
    private Date endTime;

    private String location;

    @JsonProperty("rsvp_status")
    private String rsvpStatus;

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link String} object
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link String} object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>startTime</code>.</p>
     *
     * @return a {@link Date} object
     */
    public Date getStartTime() {
        return newDate(startTime);
    }

    /**
     * <p>Setter for the field <code>startTime</code>.</p>
     *
     * @param startTime a {@link Date} object
     */
    public void setStartTime(Date startTime) {
        this.startTime = newDate(startTime);
    }

    /**
     * <p>Getter for the field <code>endTime</code>.</p>
     *
     * @return a {@link Date} object
     */
    public Date getEndTime() {
        return newDate(endTime);
    }

    /**
     * <p>Setter for the field <code>endTime</code>.</p>
     *
     * @param endTime a {@link Date} object
     */
    public void setEndTime(Date endTime) {
        this.endTime = newDate(endTime);
    }

    /**
     * <p>Getter for the field <code>location</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getLocation() {
        return location;
    }

    /**
     * <p>Setter for the field <code>location</code>.</p>
     *
     * @param location a {@link String} object
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * <p>Getter for the field <code>rsvpStatus</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getRsvpStatus() {
        return rsvpStatus;
    }

    /**
     * <p>Setter for the field <code>rsvpStatus</code>.</p>
     *
     * @param rsvpStatus a {@link String} object
     */
    public void setRsvpStatus(String rsvpStatus) {
        this.rsvpStatus = rsvpStatus;
    }
}
