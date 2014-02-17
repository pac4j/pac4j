/*
  Copyright 2012 - 2014 Jerome Leleu

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
package org.pac4j.oauth.profile.twitter;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * This class defines the attributes of the Twitter profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class TwitterAttributesDefinition extends OAuthAttributesDefinition {
    
    public static final String CONTRIBUTORS_ENABLED = "contributors_enabled";
    public static final String CREATED_AT = "created_at";
    public static final String DEFAULT_PROFILE = "default_profile";
    public static final String DEFAULT_PROFILE_IMAGE = "default_profile_image";
    public static final String DESCRIPTION = "description";
    public static final String FAVOURITES_COUNT = "favourites_count";
    public static final String FOLLOW_REQUEST_SENT = "follow_request_sent";
    public static final String FOLLOWERS_COUNT = "followers_count";
    public static final String FOLLOWING = "following";
    public static final String FRIENDS_COUNT = "friends_count";
    public static final String GEO_ENABLED = "geo_enabled";
    public static final String IS_TRANSLATOR = "is_translator";
    public static final String LANG = "lang";
    public static final String LISTED_COUNT = "listed_count";
    public static final String LOCATION = "location";
    public static final String NAME = "name";
    public static final String NOTIFICATIONS = "notifications";
    public static final String PROFILE_BACKGROUND_COLOR = "profile_background_color";
    public static final String PROFILE_BACKGROUND_IMAGE_URL = "profile_background_image_url";
    public static final String PROFILE_BACKGROUND_IMAGE_URL_HTTPS = "profile_background_image_url_https";
    public static final String PROFILE_BACKGROUND_TILE = "profile_background_tile";
    public static final String PROFILE_IMAGE_URL = "profile_image_url";
    public static final String PROFILE_IMAGE_URL_HTTPS = "profile_image_url_https";
    public static final String PROFILE_LINK_COLOR = "profile_link_color";
    public static final String PROFILE_SIDEBAR_BORDER_COLOR = "profile_sidebar_border_color";
    public static final String PROFILE_SIDEBAR_FILL_COLOR = "profile_sidebar_fill_color";
    public static final String PROFILE_TEXT_COLOR = "profile_text_color";
    public static final String PROFILE_USE_BACKGROUND_IMAGE = "profile_use_background_image";
    public static final String PROTECTED = "protected";
    public static final String SCREEN_NAME = "screen_name";
    public static final String SHOW_ALL_INLINE_MEDIA = "show_all_inline_media";
    public static final String STATUSES_COUNT = "statuses_count";
    public static final String TIME_ZONE = "time_zone";
    public static final String URL = "url";
    public static final String UTC_OFFSET = "utc_offset";
    public static final String VERIFIED = "verified";
    
    public TwitterAttributesDefinition() {
        addAttribute(CONTRIBUTORS_ENABLED, Converters.booleanConverter);
        addAttribute(CREATED_AT, TwitterConverters.dateConverter);
        addAttribute(DEFAULT_PROFILE, Converters.booleanConverter);
        addAttribute(DEFAULT_PROFILE_IMAGE, Converters.booleanConverter);
        addAttribute(DESCRIPTION, Converters.stringConverter);
        addAttribute(FAVOURITES_COUNT, Converters.integerConverter);
        addAttribute(FOLLOW_REQUEST_SENT, Converters.booleanConverter);
        addAttribute(FOLLOWERS_COUNT, Converters.integerConverter);
        addAttribute(FOLLOWING, Converters.booleanConverter);
        addAttribute(FRIENDS_COUNT, Converters.integerConverter);
        addAttribute(GEO_ENABLED, Converters.booleanConverter);
        addAttribute(IS_TRANSLATOR, Converters.booleanConverter);
        addAttribute(LANG, Converters.localeConverter);
        addAttribute(LISTED_COUNT, Converters.integerConverter);
        addAttribute(LOCATION, Converters.stringConverter);
        addAttribute(NAME, Converters.stringConverter);
        addAttribute(NOTIFICATIONS, Converters.booleanConverter);
        addAttribute(PROFILE_BACKGROUND_COLOR, Converters.colorConverter);
        addAttribute(PROFILE_BACKGROUND_IMAGE_URL, Converters.urlConverter);
        addAttribute(PROFILE_BACKGROUND_IMAGE_URL_HTTPS, Converters.urlConverter);
        addAttribute(PROFILE_BACKGROUND_TILE, Converters.booleanConverter);
        addAttribute(PROFILE_IMAGE_URL, Converters.urlConverter);
        addAttribute(PROFILE_IMAGE_URL_HTTPS, Converters.urlConverter);
        addAttribute(PROFILE_LINK_COLOR, Converters.colorConverter);
        addAttribute(PROFILE_SIDEBAR_BORDER_COLOR, Converters.colorConverter);
        addAttribute(PROFILE_SIDEBAR_FILL_COLOR, Converters.colorConverter);
        addAttribute(PROFILE_TEXT_COLOR, Converters.colorConverter);
        addAttribute(PROFILE_USE_BACKGROUND_IMAGE, Converters.booleanConverter);
        addAttribute(PROTECTED, Converters.booleanConverter);
        addAttribute(SCREEN_NAME, Converters.stringConverter);
        addAttribute(SHOW_ALL_INLINE_MEDIA, Converters.booleanConverter);
        addAttribute(STATUSES_COUNT, Converters.integerConverter);
        addAttribute(TIME_ZONE, Converters.stringConverter);
        addAttribute(URL, Converters.stringConverter);
        addAttribute(UTC_OFFSET, Converters.integerConverter);
        addAttribute(VERIFIED, Converters.booleanConverter);
    }
}
