package org.pac4j.oauth.profile.facebook;

import java.io.Serial;
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

    @Serial
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
     * <p>Getter for the field <code>from</code>.</p>
     *
     * @return a {@link FacebookObject} object
     */
    public FacebookObject getFrom() {
        return from;
    }

    /**
     * <p>Setter for the field <code>from</code>.</p>
     *
     * @param from a {@link FacebookObject} object
     */
    public void setFrom(FacebookObject from) {
        this.from = from;
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
     * <p>Getter for the field <code>publishTime</code>.</p>
     *
     * @return a {@link Date} object
     */
    public Date getPublishTime() {
        return newDate(publishTime);
    }

    /**
     * <p>Setter for the field <code>publishTime</code>.</p>
     *
     * @param publishTime a {@link Date} object
     */
    public void setPublishTime(Date publishTime) {
        this.publishTime = newDate(publishTime);
    }

    /**
     * <p>Getter for the field <code>application</code>.</p>
     *
     * @return a {@link FacebookApplication} object
     */
    public FacebookApplication getApplication() {
        return application;
    }

    /**
     * <p>Setter for the field <code>application</code>.</p>
     *
     * @param application a {@link FacebookApplication} object
     */
    public void setApplication(FacebookApplication application) {
        this.application = application;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getType() {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link String} object
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * <p>Getter for the field <code>noFeedStory</code>.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getNoFeedStory() {
        return noFeedStory;
    }

    /**
     * <p>Setter for the field <code>noFeedStory</code>.</p>
     *
     * @param noFeedStory a {@link Boolean} object
     */
    public void setNoFeedStory(Boolean noFeedStory) {
        this.noFeedStory = noFeedStory;
    }

    /**
     * <p>Getter for the field <code>song</code>.</p>
     *
     * @return a {@link FacebookMusicData} object
     */
    public FacebookMusicData getSong() {
        return song;
    }

    /**
     * <p>Setter for the field <code>song</code>.</p>
     *
     * @param song a {@link FacebookMusicData} object
     */
    public void setSong(FacebookMusicData song) {
        this.song = song;
    }

    /**
     * <p>Getter for the field <code>musician</code>.</p>
     *
     * @return a {@link FacebookMusicData} object
     */
    public FacebookMusicData getMusician() {
        return musician;
    }

    /**
     * <p>Setter for the field <code>musician</code>.</p>
     *
     * @param musician a {@link FacebookMusicData} object
     */
    public void setMusician(FacebookMusicData musician) {
        this.musician = musician;
    }

    /**
     * <p>Getter for the field <code>radioStation</code>.</p>
     *
     * @return a {@link FacebookMusicData} object
     */
    public FacebookMusicData getRadioStation() {
        return radioStation;
    }

    /**
     * <p>Setter for the field <code>radioStation</code>.</p>
     *
     * @param radioStation a {@link FacebookMusicData} object
     */
    public void setRadioStation(FacebookMusicData radioStation) {
        this.radioStation = radioStation;
    }
}
