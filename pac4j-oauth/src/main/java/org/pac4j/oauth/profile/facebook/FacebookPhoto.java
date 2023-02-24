package org.pac4j.oauth.profile.facebook;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import static org.pac4j.core.util.CommonHelper.newDate;

/**
 * This class represents a Facebook photo.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class FacebookPhoto implements Serializable {

    private static final long serialVersionUID = -1230468571423177489L;

    private String id;

    private FacebookObject from;

    private String name;

    private String link;

    private String coverPhoto;

    private String privacy;

    private Integer count;

    private String type;

    @JsonProperty("created_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssz")
    private Date createdTime;

    @JsonProperty("updated_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssz")
    private Date updatedTime;

    private Boolean canUpload;

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
     * <p>Getter for the field <code>from</code>.</p>
     *
     * @return a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public FacebookObject getFrom() {
        return from;
    }

    /**
     * <p>Setter for the field <code>from</code>.</p>
     *
     * @param from a {@link org.pac4j.oauth.profile.facebook.FacebookObject} object
     */
    public void setFrom(FacebookObject from) {
        this.from = from;
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
     * <p>Getter for the field <code>link</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getLink() {
        return link;
    }

    /**
     * <p>Setter for the field <code>link</code>.</p>
     *
     * @param link a {@link java.lang.String} object
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * <p>Getter for the field <code>coverPhoto</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCoverPhoto() {
        return coverPhoto;
    }

    /**
     * <p>Setter for the field <code>coverPhoto</code>.</p>
     *
     * @param coverPhoto a {@link java.lang.String} object
     */
    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    /**
     * <p>Getter for the field <code>privacy</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPrivacy() {
        return privacy;
    }

    /**
     * <p>Setter for the field <code>privacy</code>.</p>
     *
     * @param privacy a {@link java.lang.String} object
     */
    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    /**
     * <p>Getter for the field <code>count</code>.</p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public Integer getCount() {
        return count;
    }

    /**
     * <p>Setter for the field <code>count</code>.</p>
     *
     * @param count a {@link java.lang.Integer} object
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getType() {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link java.lang.String} object
     */
    public void setType(String type) {
        this.type = type;
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

    /**
     * <p>Getter for the field <code>updatedTime</code>.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getUpdatedTime() {
        return newDate(updatedTime);
    }

    /**
     * <p>Setter for the field <code>updatedTime</code>.</p>
     *
     * @param updatedTime a {@link java.util.Date} object
     */
    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = newDate(updatedTime);
    }

    /**
     * <p>Getter for the field <code>canUpload</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean getCanUpload() {
        return canUpload;
    }

    /**
     * <p>Setter for the field <code>canUpload</code>.</p>
     *
     * @param canUpload a {@link java.lang.Boolean} object
     */
    public void setCanUpload(Boolean canUpload) {
        this.canUpload = canUpload;
    }
}
