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
import org.scribe.builder.api.TwitterApi;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.BaseOAuth10Provider;

/**
 * This class is the OAuth provider to authenticate user in Twitter.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TwitterProvider extends BaseOAuth10Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(TwitterApi.class).apiKey(key).apiSecret(secret).callback(callbackUrl)
            .build();
        // https://dev.twitter.com/docs/api/1/get/account/verify_credentials
        String[] names = new String[] {
            "lang", "profile_background_tile", "protected", "listed_count", "geo_enabled",
            "profile_sidebar_fill_color", "name", "statuses_count", "followers_count", "profile_image_url",
            "default_profile", "show_all_inline_media", "follow_request_sent", "utc_offset", "created_at",
            "profile_sidebar_border_color", "description", "following", "notifications",
            "profile_use_background_image", "time_zone", "friends_count", "screen_name", "contributors_enabled",
            "verified", "profile_text_color", "default_profile_image", "profile_background_image_url",
            "profile_background_image_url_https", "favourites_count", "profile_link_color", "location",
            "is_translator", "profile_image_url_https", "profile_background_color", "url"
        };
        for (String name : names) {
            mainAttributes.put(name, null);
        }
    }
    
    @Override
    protected String getProfileUrl() {
        return "http://api.twitter.com/1/account/verify_credentials.json";
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        UserProfile userProfile = new UserProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profileHelper.addIdentifier(userProfile, json, "id_str");
            for (String attribute : mainAttributes.keySet()) {
                profileHelper.addAttribute(userProfile, json, attribute, mainAttributes.get(attribute));
            }
        }
        return userProfile;
    }
}
