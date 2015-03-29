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

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.XmlHelper;
import org.pac4j.oauth.profile.XmlObject;

/**
 * This class represents a LinkedIn position.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2Position extends XmlObject {
    
    private static final long serialVersionUID = 5545320712620544612L;
    
    private String id;
    
    private String title;
    
    private String summary;
    
    private Boolean isCurrent;
    
    private LinkedIn2Date startDate;
    
    private LinkedIn2Date endDate;
    
    private LinkedIn2Company company;
    
    @Override
    protected void buildFromXml(final String xml) {
        this.id = XmlHelper.get(xml, "id");
        this.title = XmlHelper.get(xml, "title");
        this.summary = XmlHelper.get(xml, "summary");
        this.isCurrent = (Boolean) XmlHelper.convert(Converters.booleanConverter, xml, "is-current");
        this.startDate = (LinkedIn2Date) XmlHelper.convert(LinkedIn2Converters.dateConverter, xml, "start-date");
        this.endDate = (LinkedIn2Date) XmlHelper.convert(LinkedIn2Converters.dateConverter, xml, "end-date");
        this.company = (LinkedIn2Company) XmlHelper.convert(LinkedIn2Converters.companyConverter, xml, "company");
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public String getSummary() {
        return this.summary;
    }
    
    public Boolean getIsCurrent() {
        return this.isCurrent;
    }
    
    public LinkedIn2Date getStartDate() {
        return this.startDate;
    }
    
    public LinkedIn2Date getEndDate() {
        return this.endDate;
    }
    
    public LinkedIn2Company getCompany() {
        return this.company;
    }
}
