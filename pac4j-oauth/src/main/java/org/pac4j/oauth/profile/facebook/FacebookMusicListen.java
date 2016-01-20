/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.oauth.profile.facebook;

import org.pac4j.oauth.profile.JsonObject;

import java.util.Date;

/**
 * This class represents a Facebook music listened.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookMusicListen extends JsonObject {
    
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

    public void setId(String id) {
        this.id = id;
    }

    public FacebookObject getFrom() {
        return from;
    }

    public void setFrom(FacebookObject from) {
        this.from = from;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public FacebookApplication getApplication() {
        return application;
    }

    public void setApplication(FacebookApplication application) {
        this.application = application;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getNoFeedStory() {
        return noFeedStory;
    }

    public void setNoFeedStory(Boolean noFeedStory) {
        this.noFeedStory = noFeedStory;
    }

    public FacebookMusicData getSong() {
        return song;
    }

    public void setSong(FacebookMusicData song) {
        this.song = song;
    }

    public FacebookMusicData getMusician() {
        return musician;
    }

    public void setMusician(FacebookMusicData musician) {
        this.musician = musician;
    }

    public FacebookMusicData getRadioStation() {
        return radioStation;
    }

    public void setRadioStation(FacebookMusicData radioStation) {
        this.radioStation = radioStation;
    }
}
