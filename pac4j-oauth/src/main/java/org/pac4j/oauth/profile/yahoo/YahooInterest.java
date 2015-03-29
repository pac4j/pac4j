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
package org.pac4j.oauth.profile.yahoo;

import java.util.List;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;
import org.pac4j.oauth.profile.converter.JsonConverters;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a Yahoo interest.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooInterest extends JsonObject {
    
    private static final long serialVersionUID = 3613314161531695788L;
    
    private List<String> declaredInterests;
    
    private String interestCategory;
    
    @SuppressWarnings("unchecked")
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.interestCategory = (String) JsonHelper.convert(Converters.stringConverter, json, "interestCategory");
        this.declaredInterests = (List<String>) JsonHelper.convert(JsonConverters.listStringConverter, json,
                                                                   "declaredInterests");
    }
    
    public List<String> getDeclaredInterests() {
        return this.declaredInterests;
    }
    
    public String getInterestCategory() {
        return this.interestCategory;
    }
}
