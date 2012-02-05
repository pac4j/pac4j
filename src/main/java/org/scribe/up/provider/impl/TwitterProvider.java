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

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.UserProfileHelper;
import org.scribe.up.provider.BaseOAuth10Provider;

/**
 * This class is the Twitter provider to authenticate user in Twitter. It extends the <b>BaseOAuth10Provider</b> class.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public class TwitterProvider extends BaseOAuth10Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(TwitterApi.class).apiKey(key).apiSecret(secret).callback(callbackUrl)
            .build();
    }
    
    @Override
    protected String getProfileUrl() {
        return "http://api.twitter.com/1/account/verify_credentials.json";
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        UserProfile userProfile = new UserProfile();
        try {
            JSONObject json = new JSONObject(body);
            UserProfileHelper.addIdentifier(userProfile, json, "id_str");
            UserProfileHelper.addAttribute(userProfile, json, "lang");
            UserProfileHelper.addAttribute(userProfile, json, "profile_background_tile");
            UserProfileHelper.addAttribute(userProfile, json, "protected");
            UserProfileHelper.addAttribute(userProfile, json, "listed_count");
            UserProfileHelper.addAttribute(userProfile, json, "geo_enabled");
            UserProfileHelper.addAttribute(userProfile, json, "profile_sidebar_fill_color");
            UserProfileHelper.addAttribute(userProfile, json, "name");
            UserProfileHelper.addAttribute(userProfile, json, "statuses_count");
            UserProfileHelper.addAttribute(userProfile, json, "followers_count");
            UserProfileHelper.addAttribute(userProfile, json, "profile_image_url");
            UserProfileHelper.addAttribute(userProfile, json, "default_profile");
            UserProfileHelper.addAttribute(userProfile, json, "show_all_inline_media");
            UserProfileHelper.addAttribute(userProfile, json, "follow_request_sent");
            UserProfileHelper.addAttribute(userProfile, json, "utc_offset");
            UserProfileHelper.addAttribute(userProfile, json, "created_at");
            UserProfileHelper.addAttribute(userProfile, json, "profile_sidebar_border_color");
            UserProfileHelper.addAttribute(userProfile, json, "description");
            UserProfileHelper.addAttribute(userProfile, json, "following");
            UserProfileHelper.addAttribute(userProfile, json, "notifications");
            UserProfileHelper.addAttribute(userProfile, json, "profile_use_background_image");
            UserProfileHelper.addAttribute(userProfile, json, "time_zone");
            UserProfileHelper.addAttribute(userProfile, json, "friends_count");
            UserProfileHelper.addAttribute(userProfile, json, "screen_name");
            UserProfileHelper.addAttribute(userProfile, json, "contributors_enabled");
            UserProfileHelper.addAttribute(userProfile, json, "verified");
            UserProfileHelper.addAttribute(userProfile, json, "profile_text_color");
            UserProfileHelper.addAttribute(userProfile, json, "default_profile_image");
            UserProfileHelper.addAttribute(userProfile, json, "profile_background_image_url");
            UserProfileHelper.addAttribute(userProfile, json, "profile_background_image_url_https");
            UserProfileHelper.addAttribute(userProfile, json, "favourites_count");
            UserProfileHelper.addAttribute(userProfile, json, "profile_link_color");
            UserProfileHelper.addAttribute(userProfile, json, "location");
            UserProfileHelper.addAttribute(userProfile, json, "is_translator");
            UserProfileHelper.addAttribute(userProfile, json, "profile_image_url_https");
            UserProfileHelper.addAttribute(userProfile, json, "profile_background_color");
            UserProfileHelper.addAttribute(userProfile, json, "url");
        } catch (JSONException e) {
            logger.error("JSON exception", e);
        }
        return userProfile;
    }
}
