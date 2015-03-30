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
package org.pac4j.oauth.profile.foursquare;

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

/**
 * This class represents a Foursquare user photo.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareUserPhoto extends JsonObject {

    private static final long serialVersionUID = -6808386671187616407L;

    private String photoUrl;

    @Override
    protected void buildFromJson(JsonNode json) {
        photoUrl = JsonHelper.get(json,"prefix") + "original" + JsonHelper.get(json,"suffix");
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
