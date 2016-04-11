package org.pac4j.oauth.profile.twitter;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.FormattedDateConverter;

import java.util.Arrays;
import java.util.Locale;

/**
 * This class defines the attributes of the Twitter profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class TwitterAttributesDefinition extends AttributesDefinition {
    
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
        Arrays.stream(new String[] {DESCRIPTION, LOCATION, NAME, SCREEN_NAME, TIME_ZONE, URL})
                .forEach(a -> primary(a, Converters.STRING));
        Arrays.stream(new String[] {CONTRIBUTORS_ENABLED, DEFAULT_PROFILE, DEFAULT_PROFILE_IMAGE, FOLLOW_REQUEST_SENT, FOLLOWING,
                GEO_ENABLED, IS_TRANSLATOR, NOTIFICATIONS, PROFILE_USE_BACKGROUND_IMAGE, PROTECTED, SHOW_ALL_INLINE_MEDIA,
                PROFILE_BACKGROUND_TILE, VERIFIED})
                .forEach(a -> primary(a, Converters.BOOLEAN));
        Arrays.stream(new String[] {FAVOURITES_COUNT, FOLLOWERS_COUNT, FRIENDS_COUNT, LISTED_COUNT, STATUSES_COUNT, UTC_OFFSET})
                .forEach(a -> primary(a, Converters.INTEGER));
        Arrays.stream(new String[] {PROFILE_BACKGROUND_IMAGE_URL, PROFILE_BACKGROUND_IMAGE_URL_HTTPS,
                PROFILE_IMAGE_URL, PROFILE_IMAGE_URL_HTTPS}).forEach(a -> primary(a, Converters.URL));
        Arrays.stream(new String[] {PROFILE_BACKGROUND_COLOR, PROFILE_LINK_COLOR, PROFILE_SIDEBAR_BORDER_COLOR,
                PROFILE_SIDEBAR_FILL_COLOR, PROFILE_TEXT_COLOR}).forEach(a -> primary(a, Converters.COLOR));
        primary(LANG, Converters.LOCALE);
        primary(CREATED_AT, new FormattedDateConverter("EEE MMM dd HH:mm:ss Z yyyy", Locale.US));
    }
}
