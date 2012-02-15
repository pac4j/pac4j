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
import org.scribe.builder.api.FacebookApi;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.BaseOAuth20Provider;

/**
 * This class is the OAuth provider to authenticate user in Facebook.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class FacebookProvider extends BaseOAuth20Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(FacebookApi.class).apiKey(key).apiSecret(secret).callback(callbackUrl)
            .build();
        String[] names = new String[] {
            "name", "first_name", "last_name", "link", "gender", "email", "timezone", "locale", "verified",
            "updated_time"
        };
        for (String name : names) {
            mainAttributes.put(name, null);
        }
    }
    
    @Override
    protected String getProfileUrl() {
        return "https://graph.facebook.com/me";
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        UserProfile userProfile = new UserProfile();
        JsonNode json = profileHelper.getFirstJsonNode(body);
        if (json != null) {
            profileHelper.addIdentifier(userProfile, json, "id");
            for (String attribute : mainAttributes.keySet()) {
                profileHelper.addAttribute(userProfile, json, attribute, mainAttributes.get(attribute));
            }
        }
        return userProfile;
    }
}
