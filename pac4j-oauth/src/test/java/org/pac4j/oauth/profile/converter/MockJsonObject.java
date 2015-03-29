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
package org.pac4j.oauth.profile.converter;

import org.pac4j.oauth.profile.JsonObject;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is a mock for JsonObject.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class MockJsonObject extends JsonObject {
    
    private static final long serialVersionUID = -5424325226224232822L;
    
    private String value;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.value = json.textValue();
    }
    
    public String getValue() {
        return this.value;
    }
}
