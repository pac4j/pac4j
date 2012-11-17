/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.profile.facebook;

import java.util.Date;

import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

import com.fasterxml.jackson.databind.JsonNode;

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
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.id = Converters.stringConverter.convertFromJson(json, "id");
        this.from = (FacebookObject) FacebookConverters.objectConverter.convertFromJson(json, "from");
        this.startTime = Converters.dateConverter.convertFromJson(json, "start_time");
        this.endTime = Converters.dateConverter.convertFromJson(json, "end_time");
        this.publishTime = Converters.dateConverter.convertFromJson(json, "publish_time");
        this.application = (FacebookApplication) FacebookConverters.applicationConverter.convertFromJson(json,
                                                                                                         "application");
        final JsonNode data = (JsonNode) JsonHelper.get(json, "data");
        if (data != null) {
            this.song = (FacebookMusicData) FacebookConverters.musicDataConverter.convertFromJson(data, "song");
            this.musician = (FacebookMusicData) FacebookConverters.musicDataConverter.convertFromJson(data, "musician");
            this.radioStation = (FacebookMusicData) FacebookConverters.musicDataConverter
                .convertFromJson(data, "radio_station");
        }
        this.type = Converters.stringConverter.convertFromJson(json, "type");
        this.noFeedStory = Converters.booleanConverter.convertFromJson(json, "no_feed_story");
    }
    
    public String getId() {
        return this.id;
    }
    
    public FacebookObject getFrom() {
        return this.from;
    }
    
    public Date getStartTime() {
        return this.startTime;
    }
    
    public Date getEndTime() {
        return this.endTime;
    }
    
    public Date getPublishTime() {
        return this.publishTime;
    }
    
    public FacebookApplication getApplication() {
        return this.application;
    }
    
    public FacebookMusicData getSong() {
        return this.song;
    }
    
    public FacebookMusicData getMusician() {
        return this.musician;
    }
    
    public FacebookMusicData getRadioStation() {
        return this.radioStation;
    }
    
    public String getType() {
        return this.type;
    }
    
    public Boolean getNoFeedStory() {
        return this.noFeedStory;
    }
}
