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
 * <p>This class is the user profile for Foursquare with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.FoursquareClient}.</p>
 * 
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareUserContact extends JsonObject {

    private static final long serialVersionUID = -4866834192367416908L;
 
    private String email;
    private String twitter;
    private String facebook;

    @Override
    protected void buildFromJson(JsonNode json) {
        email = (String) JsonHelper.get(json, "email");
        twitter = (String) JsonHelper.get(json, "twitter");
        facebook = (String) JsonHelper.get(json, "facebook");
    }

    public String getEmail() {
        return email;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getFacebook() {
        return facebook;
    }
}
