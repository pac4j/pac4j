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
package org.pac4j.oauth.profile.linkedin2;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pac4j.oauth.profile.JsonObject;

/**
 * This class represents a LinkedIn position.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2Position extends JsonObject {
    
    private static final long serialVersionUID = 5545320712620544612L;
    
    private String id;
    
    private String title;
    
    private String summary;

    @JsonProperty("isCurrent")
    private Boolean isCurrent;

    @JsonProperty("startDate")
    private LinkedIn2Date startDate;

    @JsonProperty("endDate")
    private LinkedIn2Date endDate;
    
    private LinkedIn2Company company;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Boolean getCurrent() {
        return isCurrent;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }

    public LinkedIn2Date getStartDate() {
        return startDate;
    }

    public void setStartDate(LinkedIn2Date startDate) {
        this.startDate = startDate;
    }

    public LinkedIn2Date getEndDate() {
        return endDate;
    }

    public void setEndDate(LinkedIn2Date endDate) {
        this.endDate = endDate;
    }

    public LinkedIn2Company getCompany() {
        return company;
    }

    public void setCompany(LinkedIn2Company company) {
        this.company = company;
    }
}
