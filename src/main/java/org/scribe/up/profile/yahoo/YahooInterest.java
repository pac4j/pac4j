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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfileHelper;

/**
 * This class represents an interest for Yahoo.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooInterest {
    
    private List<String> declaredInterests = new ArrayList<String>();
    
    private String interestCategory;
    
    @SuppressWarnings("unchecked")
    public YahooInterest(JsonNode json) {
        if (json != null) {
            this.interestCategory = JsonHelper.getTextValue(json, "interestCategory");
            json = json.get("declaredInterests");
            this.declaredInterests = (List<String>) UserProfileHelper.getListObject(json, String.class);
        }
    }
    
    public List<String> getDeclaredInterests() {
        return declaredInterests;
    }
    
    public String getInterestCategory() {
        return interestCategory;
    }
    
    @Override
    public String toString() {
        return "YahooInterest(declaredInterests:" + declaredInterests + ",interestCategory:" + interestCategory + ")";
    }
}
