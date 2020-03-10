package org.pac4j.oauth.profile.facebook;

import java.io.Serializable;
import java.util.Date;

import static org.pac4j.core.util.CommonHelper.newDate;

/**
 * This class represents a Facebook music listened.
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookMusicListen implements Serializable {

    private static final long serialVersionUID = 3904637830042371121L;

    private String id;

    private FacebookObject from;

    private Date startTime;

    private Date endTime;

    private Date publishTime;

    private FacebookApplication application;

    private FacebookMusicData song;

    private FacebookMusicData musician;

    private FacebookMusicData radioStation;

    private String type;

    private Boolean noFeedStory;

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

    public Date getPublishTime() {
        return newDate(publishTime);
    }

    public void setPublishTime(final Date publishTime) {
        this.publishTime = newDate(publishTime);
    }

    public FacebookApplication getApplication() {
        return application;
    }

    public void setApplication(final FacebookApplication application) {
        this.application = application;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Boolean getNoFeedStory() {
        return noFeedStory;
    }

    public void setNoFeedStory(final Boolean noFeedStory) {
        this.noFeedStory = noFeedStory;
    }

    public FacebookMusicData getSong() {
        return song;
    }

    public void setSong(final FacebookMusicData song) {
        this.song = song;
    }

    public FacebookMusicData getMusician() {
        return musician;
    }

    public void setMusician(final FacebookMusicData musician) {
        this.musician = musician;
    }

    public FacebookMusicData getRadioStation() {
        return radioStation;
    }

    public void setRadioStation(final FacebookMusicData radioStation) {
        this.radioStation = radioStation;
    }
}
