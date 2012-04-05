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
package org.scribe.up.profile.twitter;

import java.util.Locale;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.converter.Converters;
import org.scribe.up.profile.converter.FormattedDateConverter;
import org.scribe.up.profile.converter.StringReplaceConverter;

/**
 * This class defines the attributes of the Twitter profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class TwitterProfileDefinition extends AttributesDefinition {
    
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
    
    public TwitterProfileDefinition() {
        attributes.add(CONTRIBUTORS_ENABLED);
        converters.put(CONTRIBUTORS_ENABLED, Converters.booleanConverter);
        attributes.add(CREATED_AT);
        converters.put(CREATED_AT, new FormattedDateConverter("EEE MMM dd HH:mm:ss Z yyyy", Locale.US));
        attributes.add(DEFAULT_PROFILE);
        converters.put(DEFAULT_PROFILE, Converters.booleanConverter);
        attributes.add(DEFAULT_PROFILE_IMAGE);
        converters.put(DEFAULT_PROFILE_IMAGE, Converters.booleanConverter);
        attributes.add(DESCRIPTION);
        converters.put(DESCRIPTION, Converters.stringConverter);
        attributes.add(FAVOURITES_COUNT);
        converters.put(FAVOURITES_COUNT, Converters.integerConverter);
        attributes.add(FOLLOW_REQUEST_SENT);
        converters.put(FOLLOW_REQUEST_SENT, Converters.booleanConverter);
        attributes.add(FOLLOWERS_COUNT);
        converters.put(FOLLOWERS_COUNT, Converters.integerConverter);
        attributes.add(FOLLOWING);
        converters.put(FOLLOWING, Converters.booleanConverter);
        attributes.add(FRIENDS_COUNT);
        converters.put(FRIENDS_COUNT, Converters.integerConverter);
        attributes.add(GEO_ENABLED);
        converters.put(GEO_ENABLED, Converters.booleanConverter);
        attributes.add(IS_TRANSLATOR);
        converters.put(IS_TRANSLATOR, Converters.booleanConverter);
        attributes.add(LANG);
        converters.put(LANG, Converters.localeConverter);
        attributes.add(LISTED_COUNT);
        converters.put(LISTED_COUNT, Converters.integerConverter);
        attributes.add(LOCATION);
        converters.put(LOCATION, Converters.stringConverter);
        attributes.add(NAME);
        converters.put(NAME, Converters.stringConverter);
        attributes.add(NOTIFICATIONS);
        converters.put(NOTIFICATIONS, Converters.booleanConverter);
        attributes.add(PROFILE_BACKGROUND_COLOR);
        converters.put(PROFILE_BACKGROUND_COLOR, Converters.colorConverter);
        StringReplaceConverter urlConverter = new StringReplaceConverter("\\/", "/");
        attributes.add(PROFILE_BACKGROUND_IMAGE_URL);
        converters.put(PROFILE_BACKGROUND_IMAGE_URL, urlConverter);
        attributes.add(PROFILE_BACKGROUND_IMAGE_URL_HTTPS);
        converters.put(PROFILE_BACKGROUND_IMAGE_URL_HTTPS, urlConverter);
        attributes.add(PROFILE_BACKGROUND_TILE);
        converters.put(PROFILE_BACKGROUND_TILE, Converters.booleanConverter);
        attributes.add(PROFILE_IMAGE_URL);
        converters.put(PROFILE_IMAGE_URL, urlConverter);
        attributes.add(PROFILE_IMAGE_URL_HTTPS);
        converters.put(PROFILE_IMAGE_URL_HTTPS, urlConverter);
        attributes.add(PROFILE_LINK_COLOR);
        converters.put(PROFILE_LINK_COLOR, Converters.colorConverter);
        attributes.add(PROFILE_SIDEBAR_BORDER_COLOR);
        converters.put(PROFILE_SIDEBAR_BORDER_COLOR, Converters.colorConverter);
        attributes.add(PROFILE_SIDEBAR_FILL_COLOR);
        converters.put(PROFILE_SIDEBAR_FILL_COLOR, Converters.colorConverter);
        attributes.add(PROFILE_TEXT_COLOR);
        converters.put(PROFILE_TEXT_COLOR, Converters.colorConverter);
        attributes.add(PROFILE_USE_BACKGROUND_IMAGE);
        converters.put(PROFILE_USE_BACKGROUND_IMAGE, Converters.booleanConverter);
        attributes.add(PROTECTED);
        converters.put(PROTECTED, Converters.booleanConverter);
        attributes.add(SCREEN_NAME);
        converters.put(SCREEN_NAME, Converters.stringConverter);
        attributes.add(SHOW_ALL_INLINE_MEDIA);
        converters.put(SHOW_ALL_INLINE_MEDIA, Converters.booleanConverter);
        attributes.add(STATUSES_COUNT);
        converters.put(STATUSES_COUNT, Converters.integerConverter);
        attributes.add(TIME_ZONE);
        converters.put(TIME_ZONE, Converters.stringConverter);
        attributes.add(URL);
        converters.put(URL, Converters.stringConverter);
        attributes.add(UTC_OFFSET);
        converters.put(UTC_OFFSET, Converters.integerConverter);
        attributes.add(VERIFIED);
        converters.put(VERIFIED, Converters.booleanConverter);
    }
}
