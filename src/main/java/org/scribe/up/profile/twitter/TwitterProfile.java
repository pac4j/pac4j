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

import java.awt.Color;
import java.util.Date;
import java.util.Locale;

import org.scribe.up.profile.UserProfile;
import org.scribe.up.util.ObjectHelper;

/**
 * This class is the user profile for Twitter with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class TwitterProfile extends UserProfile {
    
    public static final String ID = "id";
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
    
    public boolean isContributorsEnabled() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(CONTRIBUTORS_ENABLED), Boolean.FALSE);
    }
    
    public Date getCreatedAt() {
        return (Date) attributes.get(CREATED_AT);
    }
    
    public boolean isDefaultProfile() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(DEFAULT_PROFILE), Boolean.FALSE);
    }
    
    public boolean isDefaultProfileImage() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(DEFAULT_PROFILE_IMAGE), Boolean.FALSE);
    }
    
    public String getDescription() {
        return (String) attributes.get(DESCRIPTION);
    }
    
    public int getFavouritesCount() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(FAVOURITES_COUNT), new Integer(0));
    }
    
    public boolean isFollowRequestSent() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(FOLLOW_REQUEST_SENT), Boolean.FALSE);
    }
    
    public int getFollowersCount() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(FOLLOWERS_COUNT), new Integer(0));
    }
    
    public boolean isFollowing() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(FOLLOWING), Boolean.FALSE);
    }
    
    public int getFriendsCount() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(FRIENDS_COUNT), new Integer(0));
    }
    
    public boolean isGeoEnabled() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(GEO_ENABLED), Boolean.FALSE);
    }
    
    public boolean isTranslator() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(IS_TRANSLATOR), Boolean.FALSE);
    }
    
    public Locale getLang() {
        return (Locale) attributes.get(LANG);
    }
    
    public int getListedCount() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(LISTED_COUNT), new Integer(0));
    }
    
    public String getLocation() {
        return (String) attributes.get(LOCATION);
    }
    
    public String getName() {
        return (String) attributes.get(NAME);
    }
    
    public boolean isNotifications() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(NOTIFICATIONS), Boolean.FALSE);
    }
    
    public Color getProfileBackgroundColor() {
        return (Color) attributes.get(PROFILE_BACKGROUND_COLOR);
    }
    
    public String getProfileBackgroundImageUrl() {
        return (String) attributes.get(PROFILE_BACKGROUND_IMAGE_URL);
    }
    
    public String getProfileBackgroundImageUrlHttps() {
        return (String) attributes.get(PROFILE_BACKGROUND_IMAGE_URL_HTTPS);
    }
    
    public boolean isProfileBackgroundTile() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(PROFILE_BACKGROUND_TILE), Boolean.FALSE);
    }
    
    public String getProfileImageUrl() {
        return (String) attributes.get(PROFILE_IMAGE_URL);
    }
    
    public String getProfileImageUrlHttps() {
        return (String) attributes.get(PROFILE_IMAGE_URL_HTTPS);
    }
    
    public Color getProfileLinkColor() {
        return (Color) attributes.get(PROFILE_LINK_COLOR);
    }
    
    public Color getProfileSidebarBorderColor() {
        return (Color) attributes.get(PROFILE_SIDEBAR_BORDER_COLOR);
    }
    
    public Color getProfileSidebarFillColor() {
        return (Color) attributes.get(PROFILE_SIDEBAR_FILL_COLOR);
    }
    
    public Color getProfileTextColor() {
        return (Color) attributes.get(PROFILE_TEXT_COLOR);
    }
    
    public boolean isProfileUseBackgroundImage() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(PROFILE_USE_BACKGROUND_IMAGE), Boolean.FALSE);
    }
    
    public boolean isProtected() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(PROTECTED), Boolean.FALSE);
    }
    
    public String getScreenName() {
        return (String) attributes.get(SCREEN_NAME);
    }
    
    public boolean isShowAllInlineMedia() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(SHOW_ALL_INLINE_MEDIA), Boolean.FALSE);
    }
    
    public int getStatusesCount() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(STATUSES_COUNT), new Integer(0));
    }
    
    public String getTimeZone() {
        return (String) attributes.get(TIME_ZONE);
    }
    
    public String getUrl() {
        return (String) attributes.get(URL);
    }
    
    public int getUtcOffset() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(UTC_OFFSET), new Integer(0));
    }
    
    public boolean isVerified() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(VERIFIED), Boolean.FALSE);
    }
}
