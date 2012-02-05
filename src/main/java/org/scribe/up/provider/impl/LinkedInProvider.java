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

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.UserProfileHelper;
import org.scribe.up.provider.BaseOAuth10Provider;

/**
 * This class is the LinkedIn provider to authenticate user in LinkedIn. It extends the <b>BaseOAuth10Provider</b> class.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public class LinkedInProvider extends BaseOAuth10Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(LinkedInApi.class).apiKey(key).apiSecret(secret).callback(callbackUrl)
            .build();
    }
    
    @Override
    protected String getProfileUrl() {
        return "http://api.linkedin.com/v1/people/~";
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        String firstName = UserProfileHelper.extractString(body, "<first-name>", "</first-name>");
        String lastName = UserProfileHelper.extractString(body, "<last-name>", "</last-name>");
        String headline = UserProfileHelper.extractString(body, "<headline>", "</headline>");
        String url = UserProfileHelper.extractString(body, "<url>", "</url>");
        String id = UserProfileHelper.extractString(url, "&amp;key=", "&amp;authToken=");
        logger.debug("id : {}", id);
        logger.debug("firstName : {}", firstName);
        logger.debug("lastName : {}", lastName);
        logger.debug("headline : {}", headline);
        logger.debug("url : {}", url);
        UserProfile userProfile = new UserProfile(id);
        userProfile.addAttribute("firstName", firstName);
        userProfile.addAttribute("lastName", lastName);
        userProfile.addAttribute("headline", headline);
        userProfile.addAttribute("url", url);
        return userProfile;
    }
}
