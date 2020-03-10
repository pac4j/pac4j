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

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public FacebookObject getFrom() {
        return from;
    }

    public void setFrom(final FacebookObject from) {
        this.from = from;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(final String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(final String privacy) {
        this.privacy = privacy;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(final Integer count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Date getCreatedTime() {
        return newDate(createdTime);
    }

    public void setCreatedTime(final Date createdTime) {
        this.createdTime = newDate(createdTime);
    }

    public Date getUpdatedTime() {
        return newDate(updatedTime);
    }

    public void setUpdatedTime(final Date updatedTime) {
        this.updatedTime = newDate(updatedTime);
    }

    public Boolean getCanUpload() {
        return canUpload;
    }

    public void setCanUpload(final Boolean canUpload) {
        this.canUpload = canUpload;
    }
}
