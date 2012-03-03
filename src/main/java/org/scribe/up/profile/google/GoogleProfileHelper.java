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
package org.scribe.up.profile.google;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;

/**
 * This class is an helper for Google profile processing.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class GoogleProfileHelper {
    
    private GoogleProfileHelper() {
    }
    
    /**
     * Return a list of GoogleObject.
     * 
     * @param json
     * @return a list of GoogleObject
     */
    public static List<GoogleObject> getListGoogleObject(JsonNode json) {
        List<GoogleObject> list = new ArrayList<GoogleObject>();
        if (json != null) {
            Iterator<JsonNode> jsonIterator = json.getElements();
            while (jsonIterator.hasNext()) {
                list.add(new GoogleObject(jsonIterator.next()));
            }
        }
        return list;
    }
}
