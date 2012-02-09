/*
  Copyright 2012 Jérôme Leleu

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
import org.scribe.up.profile.UserProfileHelper;
import org.scribe.up.provider.BaseOAuth20Provider;

/**
 * This class is the Facebook provider to authenticate user in Facebook. It extends the <b>BaseOAuth20Provider</b> class.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public class FacebookProvider extends BaseOAuth20Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(FacebookApi.class).apiKey(key).apiSecret(secret).callback(callbackUrl)
            .build();
    }
    
    @Override
    protected String getProfileUrl() {
        return "https://graph.facebook.com/me";
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        UserProfile userProfile = new UserProfile();
        try {
            JsonNode json = UserProfileHelper.getFirstNode(body);
            UserProfileHelper.addIdentifier(userProfile, json, "id");
            UserProfileHelper.addAttribute(userProfile, json, "name");
            UserProfileHelper.addAttribute(userProfile, json, "first_name");
            UserProfileHelper.addAttribute(userProfile, json, "last_name");
            UserProfileHelper.addAttribute(userProfile, json, "link");
            UserProfileHelper.addAttribute(userProfile, json, "gender");
            UserProfileHelper.addAttribute(userProfile, json, "email");
            UserProfileHelper.addAttribute(userProfile, json, "timezone");
            UserProfileHelper.addAttribute(userProfile, json, "locale");
            UserProfileHelper.addAttribute(userProfile, json, "verified");
            UserProfileHelper.addAttribute(userProfile, json, "updated_time");
        } catch (RuntimeException e) {
            logger.error("RuntimeException", e);
        }
        return userProfile;
    }
}
