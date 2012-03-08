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
package org.scribe.up.profile.yahoo;

import java.util.Date;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.DateConverter;
import org.scribe.up.profile.JsonHelper;

/**
 * This class represents a disclosure for Yahoo.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooDisclosure {
    
    private static final DateConverter dateConverter = new DateConverter("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    private String acceptance;
    
    private String name;
    
    private Date seen;
    
    private String version;
    
    public YahooDisclosure(JsonNode json) {
        if (json != null) {
            this.acceptance = JsonHelper.getTextValue(json, "acceptance");
            this.name = JsonHelper.getTextValue(json, "name");
            this.seen = dateConverter.convert(JsonHelper.getTextValue(json, "seen"));
            this.version = JsonHelper.getTextValue(json, "version");
        }
    }
    
    public String getAcceptance() {
        return acceptance;
    }
    
    public String getName() {
        return name;
    }
    
    public Date getSeen() {
        return seen;
    }
    
    public String getVersion() {
        return version;
    }
    
    @Override
    public String toString() {
        return "YahooDisclosure(acceptance:" + acceptance + ",name:" + name + ",seen:" + seen + ",version:" + version
               + ")";
    }
}
