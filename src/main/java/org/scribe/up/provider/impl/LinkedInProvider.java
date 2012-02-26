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

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.UserProfileHelper;
import org.scribe.up.provider.BaseOAuth10Provider;

/**
 * This class is the OAuth provider to authenticate user in LinkedIn.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class LinkedInProvider extends BaseOAuth10Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(LinkedInApi.class).apiKey(key).apiSecret(secret).callback(callbackUrl)
            .build();
        mainAttributes.put("first-name", null);
        mainAttributes.put("last-name", null);
        mainAttributes.put("headline", null);
        mainAttributes.put("url", null);
    }
    
    @Override
    protected String getProfileUrl() {
        return "http://api.linkedin.com/v1/people/~";
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        UserProfile userProfile = new UserProfile();
        for (String attribute : mainAttributes.keySet()) {
            String value = UserProfileHelper.substringBetween(body, "<" + attribute + ">", "</" + attribute + ">");
            UserProfileHelper.addAttribute(userProfile, attribute, value, mainAttributes.get(attribute));
            if ("url".equals(attribute)) {
                String id = UserProfileHelper.substringBetween(value, "&amp;key=", "&amp;authToken=");
                UserProfileHelper.addIdentifier(userProfile, id);
            }
        }
        return userProfile;
    }
}
