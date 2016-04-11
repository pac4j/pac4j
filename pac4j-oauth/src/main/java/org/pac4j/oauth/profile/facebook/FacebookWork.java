package org.pac4j.oauth.profile.facebook;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.pac4j.oauth.profile.JsonObject;

import static org.pac4j.core.util.CommonHelper.newDate;

/**
 * This class represents a Facebook work.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookWork extends JsonObject {

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

    public FacebookObject getEmployer() {
        return employer;
    }

    public void setEmployer(FacebookObject employer) {
        this.employer = employer;
    }

    public FacebookObject getLocation() {
        return location;
    }

    public void setLocation(FacebookObject location) {
        this.location = location;
    }

    public FacebookObject getPosition() {
        return position;
    }

    public void setPosition(FacebookObject position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return newDate(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = newDate(startDate);
    }

    public Date getEndDate() {
        return newDate(endDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = newDate(endDate);
    }
}
