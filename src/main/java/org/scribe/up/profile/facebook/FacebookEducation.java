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

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.FromJsonBuildableObject;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfileHelper;

/**
 * This class represents an education object for Facebook.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookEducation extends FromJsonBuildableObject implements Serializable {
    
    private FacebookObject school;
    
    private FacebookObject degree;
    
    private FacebookObject year;
    
    private List<FacebookObject> concentration;
    
    private String type;
    
    public FacebookEducation(String json) {
        super(json);
    }
    
    @Override
    protected void buildFromJson(JsonNode json) {
        this.school = new FacebookObject(json.get("school"));
        this.degree = new FacebookObject(json.get("degree"));
        this.year = new FacebookObject(json.get("year"));
        JsonNode jsonConcentration = json.get("concentration");
        this.concentration = (List<FacebookObject>) UserProfileHelper.getListObject(jsonConcentration,
                                                                                    FacebookObject.class);
        this.type = JsonHelper.getTextValue(json, "type");
    }
    
    public FacebookObject getSchool() {
        return school;
    }
    
    public FacebookObject getDegree() {
        return degree;
    }
    
    public FacebookObject getYear() {
        return year;
    }
    
    public List<FacebookObject> getConcentration() {
        return concentration;
    }
    
    public String getType() {
        return type;
    }
}
