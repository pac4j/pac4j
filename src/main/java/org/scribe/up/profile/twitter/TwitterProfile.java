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

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.Color;
import org.scribe.up.profile.CommonProfile;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.UserProfile;

/**
 * This class is the user profile for Twitter with appropriate getters.<br />
 * It is returned by the {@link org.scribe.up.provider.impl.TwitterProvider}.
 * <p />
 * <table border="1" cellspacing="2px">
 * <tr>
 * <th>Method :</th>
 * <th>From the JSON profile response :</th>
 * </tr>
 * <tr>
 * <th colspan="2">The attributes of the {@link org.scribe.up.profile.CommonProfile}</th>
 * </tr>
 * <tr>
 * <td>String getEmail()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getFirstName()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getFamilyName()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getDisplayName()</td>
 * <td>the <i>name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getUsername()</td>
 * <td>the <i>screen_name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Gender getGender()</td>
 * <td>{@link org.scribe.up.profile.Gender#UNSPECIFIED}</td>
 * </tr>
 * <tr>
 * <td>Locale getLocale()</td>
 * <td>the <i>lang</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getPictureUrl()</td>
 * <td>the <i>profile_image_url</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td>the <i>url</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td>the <i>location</i> attribute</td>
 * </tr>
 * <tr>
 * <th colspan="2">More specific attributes</th>
 * </tr>
 * <tr>
 * <td>boolean isContributorsEnabled()</td>
 * <td>the <i>contributors_enabled</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isContributorsEnabledDefined()</td>
 * <td>if the <i>contributors_enabled</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>Date getCreatedAt()</td>
 * <td>the <i>created_at</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isDefaultProfile()</td>
 * <td>the <i>default_profile</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isDefaultProfileDefined()</td>
 * <td>if the <i>default_profile</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>boolean isDefaultProfileImage()</td>
 * <td>the <i>default_profile_image</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isDefaultProfileImageDefined()</td>
 * <td>if the <i>default_profile_image</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>String getDescription()</td>
 * <td>the <i>description</i> attribute</td>
 * </tr>
 * <tr>
 * <td>int getFavouritesCount()</td>
 * <td>the <i>favourites_count</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isFavouritesCountDefined()</td>
 * <td>if the <i>favourites_count</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>boolean isFollowRequestSent()</td>
 * <td>the <i>follow_request_sent</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isFollowRequestSentDefined()</td>
 * <td>if the <i>follow_request_sent</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>int getFollowersCount()</td>
 * <td>the <i>followers_count</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isFollowersCountDefined()</td>
 * <td>if the <i>followers_count</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>boolean isFollowing()</td>
 * <td>the <i>following</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isFollowingDefined()</td>
 * <td>if the <i>following</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>int getFriendsCount()</td>
 * <td>the <i>friends_count</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isFriendsCountDefined()</td>
 * <td>if the <i>friends_count</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>boolean isGeoEnabled()</td>
 * <td>the <i>geo_enabled</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isGeoEnabledDefined()</td>
 * <td>if the <i>geo_enabled</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>boolean isTranslator()</td>
 * <td>the <i>is_translator</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isTranslatorDefined()</td>
 * <td>if the <i>is_translator</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>int getListedCount()</td>
 * <td>the <i>listed_count</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isListedCountDefined()</td>
 * <td>if the <i>listed_count</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>boolean isNotifications()</td>
 * <td>the <i>notifications</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isNotificationsDefined()</td>
 * <td>if the <i>notifications</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>Color getProfileBackgroundColor()</td>
 * <td>the <i>profile_background_color</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getProfileBackgroundImageUrl()</td>
 * <td>the <i>profile_background_image_url</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getProfileBackgroundImageUrlHttps()</td>
 * <td>the <i>profile_background_image_url_https</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isProfileBackgroundTile()</td>
 * <td>the <i>profile_background_tile</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isProfileBackgroundTileDefined()</td>
 * <td>if the <i>profile_background_tile</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>String getProfileImageUrlHttps()</td>
 * <td>the <i>profile_image_url_https</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Color getProfileLinkColor()</td>
 * <td>the <i>profile_link_color</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Color getProfileSidebarBorderColor()</td>
 * <td>the <i>profile_sidebar_border_color</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Color getProfileSidebarFillColor()</td>
 * <td>the <i>profile_sidebar_fill_color</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Color getProfileTextColor()</td>
 * <td>the <i>profile_text_color</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isProfileUseBackgroundImage()</td>
 * <td>the <i>profile_use_background_image</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isProfileUseBackgroundImageDefined()</td>
 * <td>if the <i>profile_use_background_image</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>boolean isProtected()</td>
 * <td>the <i>protected</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isProtectedDefined()</td>
 * <td>if the <i>protected</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>boolean isShowAllInlineMedia()</td>
 * <td>the <i>show_all_inline_media</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isShowAllInlineMediaDefined()</td>
 * <td>if the <i>show_all_inline_media</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>int getStatusesCount()</td>
 * <td>the <i>statuses_count</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isStatusesCountDefined()</td>
 * <td>if the <i>statuses_count</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>String getTimeZone()</td>
 * <td>the <i>time_zone</i> attribute</td>
 * </tr>
 * <tr>
 * <td>int getUtcOffset()</td>
 * <td>the <i>utc_offset</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isUtcOffsetDefined()</td>
 * <td>if the <i>utc_offset</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>boolean isVerified()</td>
 * <td>the <i>verified</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isVerifiedDefined()</td>
 * <td>if the <i>verified</i> attribute exists</td>
 * </tr>
 * </table>
 * 
 * @see org.scribe.up.provider.impl.TwitterProvider
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class TwitterProfile extends UserProfile implements CommonProfile {
    
    private static final long serialVersionUID = 7687300981847288027L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return AttributesDefinitions.twitterDefinition;
    }
    
    public TwitterProfile() {
        super();
    }
    
    public TwitterProfile(final Object id) {
        super(id);
    }
    
    public TwitterProfile(final Object id, final Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getEmail() {
        return null;
    }
    
    public String getFirstName() {
        return null;
    }
    
    public String getFamilyName() {
        return null;
    }
    
    public String getDisplayName() {
        return (String) this.attributes.get(TwitterAttributesDefinition.NAME);
    }
    
    public String getUsername() {
        return (String) this.attributes.get(TwitterAttributesDefinition.SCREEN_NAME);
    }
    
    public Gender getGender() {
        return Gender.UNSPECIFIED;
    }
    
    public Locale getLocale() {
        return (Locale) this.attributes.get(TwitterAttributesDefinition.LANG);
    }
    
    public String getPictureUrl() {
        return (String) this.attributes.get(TwitterAttributesDefinition.PROFILE_IMAGE_URL);
    }
    
    public String getProfileUrl() {
        return (String) this.attributes.get(TwitterAttributesDefinition.URL);
    }
    
    public String getLocation() {
        return (String) this.attributes.get(TwitterAttributesDefinition.LOCATION);
    }
    
    public boolean isContributorsEnabled() {
        return getSafeBoolean((Boolean) this.attributes.get(TwitterAttributesDefinition.CONTRIBUTORS_ENABLED));
    }
    
    public boolean isContributorsEnabledDefined() {
        return this.attributes.get(TwitterAttributesDefinition.CONTRIBUTORS_ENABLED) != null;
    }
    
    public Date getCreatedAt() {
        return (Date) this.attributes.get(TwitterAttributesDefinition.CREATED_AT);
    }
    
    public boolean isDefaultProfile() {
        return getSafeBoolean((Boolean) this.attributes.get(TwitterAttributesDefinition.DEFAULT_PROFILE));
    }
    
    public boolean isDefaultProfileDefined() {
        return this.attributes.get(TwitterAttributesDefinition.DEFAULT_PROFILE) != null;
    }
    
    public boolean isDefaultProfileImage() {
        return getSafeBoolean((Boolean) this.attributes.get(TwitterAttributesDefinition.DEFAULT_PROFILE_IMAGE));
    }
    
    public boolean isDefaultProfileImageDefined() {
        return this.attributes.get(TwitterAttributesDefinition.DEFAULT_PROFILE_IMAGE) != null;
    }
    
    public String getDescription() {
        return (String) this.attributes.get(TwitterAttributesDefinition.DESCRIPTION);
    }
    
    public int getFavouritesCount() {
        return getSafeInt((Integer) this.attributes.get(TwitterAttributesDefinition.FAVOURITES_COUNT));
    }
    
    public boolean isFavouritesCountDefined() {
        return this.attributes.get(TwitterAttributesDefinition.FAVOURITES_COUNT) != null;
    }
    
    public boolean isFollowRequestSent() {
        return getSafeBoolean((Boolean) this.attributes.get(TwitterAttributesDefinition.FOLLOW_REQUEST_SENT));
    }
    
    public boolean isFollowRequestSentDefined() {
        return this.attributes.get(TwitterAttributesDefinition.FOLLOW_REQUEST_SENT) != null;
    }
    
    public int getFollowersCount() {
        return getSafeInt((Integer) this.attributes.get(TwitterAttributesDefinition.FOLLOWERS_COUNT));
    }
    
    public boolean isFollowersCountDefined() {
        return this.attributes.get(TwitterAttributesDefinition.FOLLOWERS_COUNT) != null;
    }
    
    public boolean isFollowing() {
        return getSafeBoolean((Boolean) this.attributes.get(TwitterAttributesDefinition.FOLLOWING));
    }
    
    public boolean isFollowingDefined() {
        return this.attributes.get(TwitterAttributesDefinition.FOLLOWING) != null;
    }
    
    public int getFriendsCount() {
        return getSafeInt((Integer) this.attributes.get(TwitterAttributesDefinition.FRIENDS_COUNT));
    }
    
    public boolean isFriendsCountDefined() {
        return this.attributes.get(TwitterAttributesDefinition.FRIENDS_COUNT) != null;
    }
    
    public boolean isGeoEnabled() {
        return getSafeBoolean((Boolean) this.attributes.get(TwitterAttributesDefinition.GEO_ENABLED));
    }
    
    public boolean isGeoEnabledDefined() {
        return this.attributes.get(TwitterAttributesDefinition.GEO_ENABLED) != null;
    }
    
    public boolean isTranslator() {
        return getSafeBoolean((Boolean) this.attributes.get(TwitterAttributesDefinition.IS_TRANSLATOR));
    }
    
    public boolean isTranslatorDefined() {
        return this.attributes.get(TwitterAttributesDefinition.IS_TRANSLATOR) != null;
    }
    
    public int getListedCount() {
        return getSafeInt((Integer) this.attributes.get(TwitterAttributesDefinition.LISTED_COUNT));
    }
    
    public boolean isListedCountDefined() {
        return this.attributes.get(TwitterAttributesDefinition.LISTED_COUNT) != null;
    }
    
    public boolean isNotifications() {
        return getSafeBoolean((Boolean) this.attributes.get(TwitterAttributesDefinition.NOTIFICATIONS));
    }
    
    public boolean isNotificationsDefined() {
        return this.attributes.get(TwitterAttributesDefinition.NOTIFICATIONS) != null;
    }
    
    public Color getProfileBackgroundColor() {
        return (Color) this.attributes.get(TwitterAttributesDefinition.PROFILE_BACKGROUND_COLOR);
    }
    
    public String getProfileBackgroundImageUrl() {
        return (String) this.attributes.get(TwitterAttributesDefinition.PROFILE_BACKGROUND_IMAGE_URL);
    }
    
    public String getProfileBackgroundImageUrlHttps() {
        return (String) this.attributes.get(TwitterAttributesDefinition.PROFILE_BACKGROUND_IMAGE_URL_HTTPS);
    }
    
    public boolean isProfileBackgroundTile() {
        return getSafeBoolean((Boolean) this.attributes.get(TwitterAttributesDefinition.PROFILE_BACKGROUND_TILE));
    }
    
    public boolean isProfileBackgroundTileDefined() {
        return this.attributes.get(TwitterAttributesDefinition.PROFILE_BACKGROUND_TILE) != null;
    }
    
    public String getProfileImageUrlHttps() {
        return (String) this.attributes.get(TwitterAttributesDefinition.PROFILE_IMAGE_URL_HTTPS);
    }
    
    public Color getProfileLinkColor() {
        return (Color) this.attributes.get(TwitterAttributesDefinition.PROFILE_LINK_COLOR);
    }
    
    public Color getProfileSidebarBorderColor() {
        return (Color) this.attributes.get(TwitterAttributesDefinition.PROFILE_SIDEBAR_BORDER_COLOR);
    }
    
    public Color getProfileSidebarFillColor() {
        return (Color) this.attributes.get(TwitterAttributesDefinition.PROFILE_SIDEBAR_FILL_COLOR);
    }
    
    public Color getProfileTextColor() {
        return (Color) this.attributes.get(TwitterAttributesDefinition.PROFILE_TEXT_COLOR);
    }
    
    public boolean isProfileUseBackgroundImage() {
        return getSafeBoolean((Boolean) this.attributes.get(TwitterAttributesDefinition.PROFILE_USE_BACKGROUND_IMAGE));
    }
    
    public boolean isProfileUseBackgroundImageDefined() {
        return this.attributes.get(TwitterAttributesDefinition.PROFILE_USE_BACKGROUND_IMAGE) != null;
    }
    
    public boolean isProtected() {
        return getSafeBoolean((Boolean) this.attributes.get(TwitterAttributesDefinition.PROTECTED));
    }
    
    public boolean isProtectedDefined() {
        return this.attributes.get(TwitterAttributesDefinition.PROTECTED) != null;
    }
    
    public boolean isShowAllInlineMedia() {
        return getSafeBoolean((Boolean) this.attributes.get(TwitterAttributesDefinition.SHOW_ALL_INLINE_MEDIA));
    }
    
    public boolean isShowAllInlineMediaDefined() {
        return this.attributes.get(TwitterAttributesDefinition.SHOW_ALL_INLINE_MEDIA) != null;
    }
    
    public int getStatusesCount() {
        return getSafeInt((Integer) this.attributes.get(TwitterAttributesDefinition.STATUSES_COUNT));
    }
    
    public boolean isStatusesCountDefined() {
        return this.attributes.get(TwitterAttributesDefinition.STATUSES_COUNT) != null;
    }
    
    public String getTimeZone() {
        return (String) this.attributes.get(TwitterAttributesDefinition.TIME_ZONE);
    }
    
    public int getUtcOffset() {
        return getSafeInt((Integer) this.attributes.get(TwitterAttributesDefinition.UTC_OFFSET));
    }
    
    public boolean isUtcOffsetDefined() {
        return this.attributes.get(TwitterAttributesDefinition.UTC_OFFSET) != null;
    }
    
    public boolean isVerified() {
        return getSafeBoolean((Boolean) this.attributes.get(TwitterAttributesDefinition.VERIFIED));
    }
    
    public boolean isVerifiedDefined() {
        return this.attributes.get(TwitterAttributesDefinition.VERIFIED) != null;
    }
}
