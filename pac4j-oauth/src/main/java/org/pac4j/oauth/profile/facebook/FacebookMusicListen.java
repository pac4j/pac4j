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

import java.util.Date;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

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
        this.id = (String) JsonHelper.convert(Converters.stringConverter, json, "id");
        this.from = (FacebookObject) JsonHelper.convert(FacebookConverters.objectConverter, json, "from");
        this.startTime = (Date) JsonHelper.convert(Converters.dateConverter, json, "start_time");
        this.endTime = (Date) JsonHelper.convert(Converters.dateConverter, json, "end_time");
        this.publishTime = (Date) JsonHelper.convert(Converters.dateConverter, json, "publish_time");
        this.application = (FacebookApplication) JsonHelper.convert(FacebookConverters.applicationConverter, json,
                                                                    "application");
        final JsonNode data = (JsonNode) JsonHelper.get(json, "data");
        if (data != null) {
            this.song = (FacebookMusicData) JsonHelper.convert(FacebookConverters.musicDataConverter, data, "song");
            this.musician = (FacebookMusicData) JsonHelper.convert(FacebookConverters.musicDataConverter, data,
                                                                   "musician");
            this.radioStation = (FacebookMusicData) JsonHelper.convert(FacebookConverters.musicDataConverter, data,
                                                                       "radio_station");
        }
        this.type = (String) JsonHelper.convert(Converters.stringConverter, json, "type");
        this.noFeedStory = (Boolean) JsonHelper.convert(Converters.booleanConverter, json, "no_feed_story");
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
