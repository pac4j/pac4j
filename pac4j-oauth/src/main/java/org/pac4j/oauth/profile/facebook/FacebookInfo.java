package org.pac4j.oauth.profile.facebook;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.pac4j.oauth.profile.JsonObject;

import static org.pac4j.core.util.CommonHelper.newDate;

/**
 * This class represents a Facebook info (id + name + category + created_time).
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class FacebookInfo extends JsonObject {
    
    private static final long serialVersionUID = -6023752317085418350L;
    
    private String id;
    
    private String category;
    
    private String name;

    @JsonProperty("created_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssz")
    private Date createdTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedTime() {
        return newDate(createdTime);
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = newDate(createdTime);
    }
}
