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
package org.scribe.up.provider.impl;

import org.codehaus.jackson.JsonNode;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LiveApi;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.live.LiveProfile;
import org.scribe.up.provider.BaseOAuth20Provider;

/**
 * This class is the OAuth provider to authenticate user in Windows Live (SkyDrive, Hotmail and Messenger). Scope is not used.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.live.LiveProfile} : name (String), first_name (String), last_name
 * (String), link (String), gender (Gender), locale (Locale) and updated_time (FormattedDate).<br />
 * More information at http://msdn.microsoft.com/en-us/library/live/hh243641.aspx
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class LiveProvider extends BaseOAuth20Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(LiveApi.class).apiKey(key).apiSecret(secret).scope("wl.basic")
            .callback(callbackUrl).build();
    }
    
    @Override
    protected String getProfileUrl() {
        return "https://apis.live.net/v5.0/me";
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        LiveProfile profile = new LiveProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, "id"));
            for (String attribute : AttributesDefinitions.liveDefinition.getAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
        }
        return profile;
    }
}
