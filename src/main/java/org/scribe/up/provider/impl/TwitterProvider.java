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
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.twitter.TwitterProfile;
import org.scribe.up.provider.BaseOAuth10Provider;

/**
 * This class is the OAuth provider to authenticate user in Twitter. Scope is not used.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.twitter.TwitterProfile} : contributors_enabled (Boolean), created_at
 * (FormattedDate), default_profile (Boolean), default_profile_image (Boolean), description (String), favourites_count (Integer),
 * follow_request_sent (Boolean), followers_count (Integer), following (Boolean), friends_count (Integer), geo_enabled (Boolean),
 * is_translator (Boolean), lang (Locale), listed_count (Integer), location (String), name (String), notifications (Boolean),
 * profile_background_color (Color), profile_background_image_url (String), profile_background_image_url_https (String),
 * profile_background_tile (Boolean), profile_image_url (String), profile_image_url_https (String), profile_link_color (Color),
 * profile_sidebar_border_color (Color), profile_sidebar_fill_color (Color), profile_text_color (Color), profile_use_background_image
 * (Boolean), protected (Boolean), screen_name (String), show_all_inline_media (Boolean), statuses_count (Integer), time_zone (String), url
 * (String), utc_offset (Integer) and verified (Boolean).<br />
 * More information at https://dev.twitter.com/docs/api/1/get/account/verify_credentials
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TwitterProvider extends BaseOAuth10Provider {
    
    protected TwitterProvider newProvider() {
        return new TwitterProvider();
    }
    
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
        TwitterProfile profile = new TwitterProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, "id"));
            for (String attribute : AttributesDefinitions.twitterDefinition.getAllAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
        }
        return profile;
    }
}
