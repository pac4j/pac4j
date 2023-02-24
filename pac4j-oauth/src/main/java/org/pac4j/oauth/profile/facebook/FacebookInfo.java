package org.pac4j.oauth.profile.facebook;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import static org.pac4j.core.util.CommonHelper.newDate;

/**
 * This class represents a Facebook info (id + name + category + created_time).
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class FacebookInfo implements Serializable {

    private static final long serialVersionUID = -6023752317085418350L;

    private String id;

    private String category;

    private String name;

    @JsonProperty("created_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssz")
    private Date createdTime;

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
     * <p>Getter for the field <code>category</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCategory() {
        return category;
    }

    /**
     * <p>Setter for the field <code>category</code>.</p>
     *
     * @param category a {@link java.lang.String} object
     */
    public void setCategory(String category) {
        this.category = category;
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
     * <p>Getter for the field <code>createdTime</code>.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getCreatedTime() {
        return newDate(createdTime);
    }

    /**
     * <p>Setter for the field <code>createdTime</code>.</p>
     *
     * @param createdTime a {@link java.util.Date} object
     */
    public void setCreatedTime(Date createdTime) {
        this.createdTime = newDate(createdTime);
    }
}
