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
import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.JsonObject;

/**
 * This class represents an education object for Facebook.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookEducation extends JsonObject implements Serializable {
    
    private static final long serialVersionUID = -8349031473955546433L;
    
    private transient final static AttributesDefinition definition = new FacebookEducationDefinition();
    
    private FacebookObject school;
    
    private FacebookObject degree;
    
    private FacebookObject year;
    
    private List<FacebookObject> concentration;
    
    private String type;
    
    public FacebookEducation(Object json) {
        super(json);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void buildFromJson(JsonNode json) {
        this.school = (FacebookObject) definition.convert(json, FacebookEducationDefinition.SCHOOL);
        this.degree = (FacebookObject) definition.convert(json, FacebookEducationDefinition.DEGREE);
        this.year = (FacebookObject) definition.convert(json, FacebookEducationDefinition.YEAR);
        this.concentration = (List<FacebookObject>) definition.convert(json, FacebookEducationDefinition.CONCENTRATION);
        this.type = (String) definition.convert(json, FacebookEducationDefinition.TYPE);
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
