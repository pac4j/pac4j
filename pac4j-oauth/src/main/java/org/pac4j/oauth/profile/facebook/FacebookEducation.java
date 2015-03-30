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

import java.util.List;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents an education object for Facebook.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookEducation extends JsonObject {
    
    private static final long serialVersionUID = 3587603107957633824L;
    
    private FacebookObject school;
    
    private FacebookObject degree;
    
    private FacebookObject year;
    
    private List<FacebookObject> concentration;
    
    private String type;
    
    @SuppressWarnings("unchecked")
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.school = (FacebookObject) JsonHelper.convert(FacebookConverters.objectConverter, json, "school");
        this.degree = (FacebookObject) JsonHelper.convert(FacebookConverters.objectConverter, json, "degree");
        this.year = (FacebookObject) JsonHelper.convert(FacebookConverters.objectConverter, json, "year");
        this.concentration = (List<FacebookObject>) JsonHelper.convert(FacebookConverters.listObjectConverter, json,
                                                                       "concentration");
        this.type = (String) JsonHelper.convert(Converters.stringConverter, json, "type");
    }
    
    public FacebookObject getSchool() {
        return this.school;
    }
    
    public FacebookObject getDegree() {
        return this.degree;
    }
    
    public FacebookObject getYear() {
        return this.year;
    }
    
    public List<FacebookObject> getConcentration() {
        return this.concentration;
    }
    
    public String getType() {
        return this.type;
    }
}
