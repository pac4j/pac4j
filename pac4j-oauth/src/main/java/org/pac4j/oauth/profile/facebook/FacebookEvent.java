package org.pac4j.oauth.profile.facebook;

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

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return newDate(startTime);
    }

    public void setStartTime(final Date startTime) {
        this.startTime = newDate(startTime);
    }

    public Date getEndTime() {
        return newDate(endTime);
    }

    public void setEndTime(final Date endTime) {
        this.endTime = newDate(endTime);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getRsvpStatus() {
        return rsvpStatus;
    }

    public void setRsvpStatus(final String rsvpStatus) {
        this.rsvpStatus = rsvpStatus;
    }
}
