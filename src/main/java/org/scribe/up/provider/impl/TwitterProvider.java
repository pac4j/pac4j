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

import java.util.Locale;

import org.codehaus.jackson.JsonNode;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.up.profile.ColorConverter;
import org.scribe.up.profile.DateConverter;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.StringReplaceConverter;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.UserProfileHelper;
import org.scribe.up.profile.twitter.TwitterProfile;
import org.scribe.up.provider.BaseOAuth10Provider;

/**
 * This class is the OAuth provider to authenticate user in Twitter. Scope is not used. Attributes are defined in
 * https://dev.twitter.com/docs/api/1/get/account/verify_credentials.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.twitter.TwitterProfile} : contributors_enabled (Boolean), created_at
 * (Date), default_profile (Boolean), default_profile_image (Boolean), description (String), favourites_count (Integer), follow_request_sent
 * (Boolean), followers_count (Integer), following (Boolean), friends_count (Integer), geo_enabled (Boolean), is_translator (Boolean), lang
 * (Locale), listed_count (Integer), location (String), name (String), notifications (Boolean), profile_background_color (Color),
 * profile_background_image_url (String), profile_background_image_url_https (String), profile_background_tile (Boolean), profile_image_url
 * (String), profile_image_url_https (String), profile_link_color (Color), profile_sidebar_border_color (Color), profile_sidebar_fill_color
 * (Color), profile_text_color (Color), profile_use_background_image (Boolean), protected (Boolean), screen_name (String),
 * show_all_inline_media (Boolean), statuses_count (Integer), time_zone (String), url (String), utc_offset (Integer), verified (Boolean).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TwitterProvider extends BaseOAuth10Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(TwitterApi.class).apiKey(key).apiSecret(secret).callback(callbackUrl)
            .build();
        String[] names = new String[] {
            TwitterProfile.CONTRIBUTORS_ENABLED, TwitterProfile.DEFAULT_PROFILE, TwitterProfile.DEFAULT_PROFILE_IMAGE,
            TwitterProfile.DESCRIPTION, TwitterProfile.FAVOURITES_COUNT, TwitterProfile.FOLLOW_REQUEST_SENT,
            TwitterProfile.FOLLOWERS_COUNT, TwitterProfile.FOLLOWING, TwitterProfile.FRIENDS_COUNT,
            TwitterProfile.GEO_ENABLED, TwitterProfile.IS_TRANSLATOR, TwitterProfile.LISTED_COUNT,
            TwitterProfile.LOCATION, TwitterProfile.NAME, TwitterProfile.NOTIFICATIONS,
            TwitterProfile.PROFILE_BACKGROUND_TILE, TwitterProfile.PROFILE_USE_BACKGROUND_IMAGE,
            TwitterProfile.PROTECTED, TwitterProfile.SCREEN_NAME, TwitterProfile.SHOW_ALL_INLINE_MEDIA,
            TwitterProfile.STATUSES_COUNT, TwitterProfile.TIME_ZONE, TwitterProfile.UTC_OFFSET, TwitterProfile.VERIFIED
        };
        for (String name : names) {
            mainAttributes.put(name, null);
        }
        mainAttributes.put(TwitterProfile.CREATED_AT, new DateConverter("EEE MMM dd HH:mm:ss Z yyyy", Locale.US));
        mainAttributes.put(TwitterProfile.LANG, localeConverter);
        ColorConverter colorConverter = new ColorConverter();
        mainAttributes.put(TwitterProfile.PROFILE_BACKGROUND_COLOR, colorConverter);
        mainAttributes.put(TwitterProfile.PROFILE_TEXT_COLOR, colorConverter);
        mainAttributes.put(TwitterProfile.PROFILE_LINK_COLOR, colorConverter);
        mainAttributes.put(TwitterProfile.PROFILE_SIDEBAR_BORDER_COLOR, colorConverter);
        mainAttributes.put(TwitterProfile.PROFILE_SIDEBAR_FILL_COLOR, colorConverter);
        mainAttributes.put(TwitterProfile.PROFILE_TEXT_COLOR, colorConverter);
        StringReplaceConverter urlConverter = new StringReplaceConverter("\\/", "/");
        mainAttributes.put(TwitterProfile.PROFILE_BACKGROUND_IMAGE_URL, urlConverter);
        mainAttributes.put(TwitterProfile.PROFILE_BACKGROUND_IMAGE_URL_HTTPS, urlConverter);
        mainAttributes.put(TwitterProfile.PROFILE_IMAGE_URL, urlConverter);
        mainAttributes.put(TwitterProfile.PROFILE_IMAGE_URL_HTTPS, urlConverter);
        mainAttributes.put(TwitterProfile.URL, urlConverter);
        // status ?
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
            UserProfileHelper.addIdentifier(profile, json, TwitterProfile.ID);
            for (String attribute : mainAttributes.keySet()) {
                UserProfileHelper.addAttribute(profile, json, attribute, mainAttributes.get(attribute));
            }
        }
        return profile;
    }
}
