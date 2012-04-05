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
import java.util.Map;

import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.impl.TwitterProvider;

/**
 * This class is the user profile for Twitter with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class TwitterProfile extends UserProfile {
    
    private static final long serialVersionUID = 1875058433035755467L;
    
    static {
        definition = new TwitterProfileDefinition();
        providerType = TwitterProvider.TYPE;
    }
    
    public TwitterProfile() {
        super();
    }
    
    public TwitterProfile(String id) {
        super(id);
    }
    
    public TwitterProfile(String id, Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public boolean isContributorsEnabled() {
        return getSafeBoolean((Boolean) attributes.get(TwitterProfileDefinition.CONTRIBUTORS_ENABLED));
    }
    
    public boolean isContributorsEnabledDefined() {
        return attributes.get(TwitterProfileDefinition.CONTRIBUTORS_ENABLED) != null;
    }
    
    public Date getCreatedAt() {
        return (Date) attributes.get(TwitterProfileDefinition.CREATED_AT);
    }
    
    public boolean isDefaultProfile() {
        return getSafeBoolean((Boolean) attributes.get(TwitterProfileDefinition.DEFAULT_PROFILE));
    }
    
    public boolean isDefaultProfileDefined() {
        return attributes.get(TwitterProfileDefinition.DEFAULT_PROFILE) != null;
    }
    
    public boolean isDefaultProfileImage() {
        return getSafeBoolean((Boolean) attributes.get(TwitterProfileDefinition.DEFAULT_PROFILE_IMAGE));
    }
    
    public boolean isDefaultProfileImageDefined() {
        return attributes.get(TwitterProfileDefinition.DEFAULT_PROFILE_IMAGE) != null;
    }
    
    public String getDescription() {
        return (String) attributes.get(TwitterProfileDefinition.DESCRIPTION);
    }
    
    public int getFavouritesCount() {
        return getSafeInteger((Integer) attributes.get(TwitterProfileDefinition.FAVOURITES_COUNT));
    }
    
    public boolean isFavouritesCountDefined() {
        return attributes.get(TwitterProfileDefinition.FAVOURITES_COUNT) != null;
    }
    
    public boolean isFollowRequestSent() {
        return getSafeBoolean((Boolean) attributes.get(TwitterProfileDefinition.FOLLOW_REQUEST_SENT));
    }
    
    public boolean isFollowRequestSentDefined() {
        return attributes.get(TwitterProfileDefinition.FOLLOW_REQUEST_SENT) != null;
    }
    
    public int getFollowersCount() {
        return getSafeInteger((Integer) attributes.get(TwitterProfileDefinition.FOLLOWERS_COUNT));
    }
    
    public boolean isFollowersCountDefined() {
        return attributes.get(TwitterProfileDefinition.FOLLOWERS_COUNT) != null;
    }
    
    public boolean isFollowing() {
        return getSafeBoolean((Boolean) attributes.get(TwitterProfileDefinition.FOLLOWING));
    }
    
    public boolean isFollowingDefined() {
        return attributes.get(TwitterProfileDefinition.FOLLOWING) != null;
    }
    
    public int getFriendsCount() {
        return getSafeInteger((Integer) attributes.get(TwitterProfileDefinition.FRIENDS_COUNT));
    }
    
    public boolean isFriendsCountDefined() {
        return attributes.get(TwitterProfileDefinition.FRIENDS_COUNT) != null;
    }
    
    public boolean isGeoEnabled() {
        return getSafeBoolean((Boolean) attributes.get(TwitterProfileDefinition.GEO_ENABLED));
    }
    
    public boolean isGeoEnabledDefined() {
        return attributes.get(TwitterProfileDefinition.GEO_ENABLED) != null;
    }
    
    public boolean isTranslator() {
        return getSafeBoolean((Boolean) attributes.get(TwitterProfileDefinition.IS_TRANSLATOR));
    }
    
    public boolean isTranslatorDefined() {
        return attributes.get(TwitterProfileDefinition.IS_TRANSLATOR) != null;
    }
    
    public Locale getLang() {
        return (Locale) attributes.get(TwitterProfileDefinition.LANG);
    }
    
    public int getListedCount() {
        return getSafeInteger((Integer) attributes.get(TwitterProfileDefinition.LISTED_COUNT));
    }
    
    public boolean isListedCountDefined() {
        return attributes.get(TwitterProfileDefinition.LISTED_COUNT) != null;
    }
    
    public String getLocation() {
        return (String) attributes.get(TwitterProfileDefinition.LOCATION);
    }
    
    public String getName() {
        return (String) attributes.get(TwitterProfileDefinition.NAME);
    }
    
    public boolean isNotifications() {
        return getSafeBoolean((Boolean) attributes.get(TwitterProfileDefinition.NOTIFICATIONS));
    }
    
    public boolean isNotificationsDefined() {
        return attributes.get(TwitterProfileDefinition.NOTIFICATIONS) != null;
    }
    
    public Color getProfileBackgroundColor() {
        return (Color) attributes.get(TwitterProfileDefinition.PROFILE_BACKGROUND_COLOR);
    }
    
    public String getProfileBackgroundImageUrl() {
        return (String) attributes.get(TwitterProfileDefinition.PROFILE_BACKGROUND_IMAGE_URL);
    }
    
    public String getProfileBackgroundImageUrlHttps() {
        return (String) attributes.get(TwitterProfileDefinition.PROFILE_BACKGROUND_IMAGE_URL_HTTPS);
    }
    
    public boolean isProfileBackgroundTile() {
        return getSafeBoolean((Boolean) attributes.get(TwitterProfileDefinition.PROFILE_BACKGROUND_TILE));
    }
    
    public boolean isProfileBackgroundTileDefined() {
        return attributes.get(TwitterProfileDefinition.PROFILE_BACKGROUND_TILE) != null;
    }
    
    public String getProfileImageUrl() {
        return (String) attributes.get(TwitterProfileDefinition.PROFILE_IMAGE_URL);
    }
    
    public String getProfileImageUrlHttps() {
        return (String) attributes.get(TwitterProfileDefinition.PROFILE_IMAGE_URL_HTTPS);
    }
    
    public Color getProfileLinkColor() {
        return (Color) attributes.get(TwitterProfileDefinition.PROFILE_LINK_COLOR);
    }
    
    public Color getProfileSidebarBorderColor() {
        return (Color) attributes.get(TwitterProfileDefinition.PROFILE_SIDEBAR_BORDER_COLOR);
    }
    
    public Color getProfileSidebarFillColor() {
        return (Color) attributes.get(TwitterProfileDefinition.PROFILE_SIDEBAR_FILL_COLOR);
    }
    
    public Color getProfileTextColor() {
        return (Color) attributes.get(TwitterProfileDefinition.PROFILE_TEXT_COLOR);
    }
    
    public boolean isProfileUseBackgroundImage() {
        return getSafeBoolean((Boolean) attributes.get(TwitterProfileDefinition.PROFILE_USE_BACKGROUND_IMAGE));
    }
    
    public boolean isProfileUseBackgroundImageDefined() {
        return attributes.get(TwitterProfileDefinition.PROFILE_USE_BACKGROUND_IMAGE) != null;
    }
    
    public boolean isProtected() {
        return getSafeBoolean((Boolean) attributes.get(TwitterProfileDefinition.PROTECTED));
    }
    
    public boolean isProtectedDefined() {
        return attributes.get(TwitterProfileDefinition.PROTECTED) != null;
    }
    
    public String getScreenName() {
        return (String) attributes.get(TwitterProfileDefinition.SCREEN_NAME);
    }
    
    public boolean isShowAllInlineMedia() {
        return getSafeBoolean((Boolean) attributes.get(TwitterProfileDefinition.SHOW_ALL_INLINE_MEDIA));
    }
    
    public boolean isShowAllInlineMediaDefined() {
        return attributes.get(TwitterProfileDefinition.SHOW_ALL_INLINE_MEDIA) != null;
    }
    
    public int getStatusesCount() {
        return getSafeInteger((Integer) attributes.get(TwitterProfileDefinition.STATUSES_COUNT));
    }
    
    public boolean isStatusesCountDefined() {
        return attributes.get(TwitterProfileDefinition.STATUSES_COUNT) != null;
    }
    
    public String getTimeZone() {
        return (String) attributes.get(TwitterProfileDefinition.TIME_ZONE);
    }
    
    public String getUrl() {
        return (String) attributes.get(TwitterProfileDefinition.URL);
    }
    
    public int getUtcOffset() {
        return getSafeInteger((Integer) attributes.get(TwitterProfileDefinition.UTC_OFFSET));
    }
    
    public boolean isUtcOffsetDefined() {
        return attributes.get(TwitterProfileDefinition.UTC_OFFSET) != null;
    }
    
    public boolean isVerified() {
        return getSafeBoolean((Boolean) attributes.get(TwitterProfileDefinition.VERIFIED));
    }
    
    public boolean isVerifiedDefined() {
        return attributes.get(TwitterProfileDefinition.VERIFIED) != null;
    }
}
