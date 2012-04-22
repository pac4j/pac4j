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

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

/**
 * This class represents a Yahoo interest.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooInterest extends JsonObject implements Serializable {
    
    private static final long serialVersionUID = 5209182421635736262L;
    
    private List<String> declaredInterests;
    
    private String interestCategory;
    
    public YahooInterest(Object json) {
        super(json);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void buildFromJson(JsonNode json) {
        this.interestCategory = Converters.stringConverter.convertFromJson(json, "interestCategory");
        this.declaredInterests = Converters.listStringConverter.convertFromJson(json, "declaredInterests");
    }
    
    public List<String> getDeclaredInterests() {
        return declaredInterests;
    }
    
    public String getInterestCategory() {
        return interestCategory;
    }
}
