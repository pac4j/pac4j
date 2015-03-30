/*
  Copyright 2012 - 2015 pac4j organization

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

import java.util.Date;
import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.Color;
import org.pac4j.oauth.profile.OAuth10Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;

/**
 * <p>This class is the user profile for Twitter with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.TwitterClient}.</p>
 * <table summary="" border="1" cellspacing="2px">
 * <tr>
 * <th>Method :</th>
 * <th>From the JSON profile response :</th>
 * </tr>
 * <tr>
 * <th colspan="2">The attributes of the {@link org.pac4j.core.profile.CommonProfile}</th>
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
 * <td>{@link org.pac4j.core.profile.Gender#UNSPECIFIED}</td>
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
 * <td>Boolean getContributorsEnabled()</td>
 * <td>the <i>contributors_enabled</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Date getCreatedAt()</td>
 * <td>the <i>created_at</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Boolean getDefaultProfile()</td>
 * <td>the <i>default_profile</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Boolean getDefaultProfileImage()</td>
 * <td>the <i>default_profile_image</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getDescription()</td>
 * <td>the <i>description</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getFavouritesCount()</td>
 * <td>the <i>favourites_count</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Boolean getFollowRequestSent()</td>
 * <td>the <i>follow_request_sent</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getFollowersCount()</td>
 * <td>the <i>followers_count</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Boolean getFollowing()</td>
 * <td>the <i>following</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getFriendsCount()</td>
 * <td>the <i>friends_count</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Boolean getGeoEnabled()</td>
 * <td>the <i>geo_enabled</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Boolean getIsTranslator()</td>
 * <td>the <i>is_translator</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getListedCount()</td>
 * <td>the <i>listed_count</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Boolean getNotifications()</td>
 * <td>the <i>notifications</i> attribute</td>
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
 * <td>Boolean getProfileBackgroundTile()</td>
 * <td>the <i>profile_background_tile</i> attribute</td>
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
 * <td>Boolean getProfileUseBackgroundImage()</td>
 * <td>the <i>profile_use_background_image</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Boolean getProtected()</td>
 * <td>the <i>protected</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Boolean getShowAllInlineMedia()</td>
 * <td>the <i>show_all_inline_media</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getStatusesCount()</td>
 * <td>the <i>statuses_count</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getTimeZone()</td>
 * <td>the <i>time_zone</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getUtcOffset()</td>
 * <td>the <i>utc_offset</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Boolean getVerified()</td>
 * <td>the <i>verified</i> attribute</td>
 * </tr>
 * </table>
 * 
 * @see org.pac4j.oauth.client.TwitterClient
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class TwitterProfile extends OAuth10Profile {
    
    private static final long serialVersionUID = -6473348745856820192L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.twitterDefinition;
    }
    
    @Override
    public String getDisplayName() {
        return (String) getAttribute(TwitterAttributesDefinition.NAME);
    }
    
    @Override
    public String getUsername() {
        return (String) getAttribute(TwitterAttributesDefinition.SCREEN_NAME);
    }
    
    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(TwitterAttributesDefinition.LANG);
    }
    
    @Override
    public String getPictureUrl() {
        return (String) getAttribute(TwitterAttributesDefinition.PROFILE_IMAGE_URL);
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(TwitterAttributesDefinition.URL);
    }
    
    public Boolean getContributorsEnabled() {
        return (Boolean) getAttribute(TwitterAttributesDefinition.CONTRIBUTORS_ENABLED);
    }
    
    public Date getCreatedAt() {
        return (Date) getAttribute(TwitterAttributesDefinition.CREATED_AT);
    }
    
    public Boolean getDefaultProfile() {
        return (Boolean) getAttribute(TwitterAttributesDefinition.DEFAULT_PROFILE);
    }
    
    public Boolean getDefaultProfileImage() {
        return (Boolean) getAttribute(TwitterAttributesDefinition.DEFAULT_PROFILE_IMAGE);
    }
    
    public String getDescription() {
        return (String) getAttribute(TwitterAttributesDefinition.DESCRIPTION);
    }
    
    public Integer getFavouritesCount() {
        return (Integer) getAttribute(TwitterAttributesDefinition.FAVOURITES_COUNT);
    }
    
    public Boolean getFollowRequestSent() {
        return (Boolean) getAttribute(TwitterAttributesDefinition.FOLLOW_REQUEST_SENT);
    }
    
    public Integer getFollowersCount() {
        return (Integer) getAttribute(TwitterAttributesDefinition.FOLLOWERS_COUNT);
    }
    
    public Boolean getFollowing() {
        return (Boolean) getAttribute(TwitterAttributesDefinition.FOLLOWING);
    }
    
    public Integer getFriendsCount() {
        return (Integer) getAttribute(TwitterAttributesDefinition.FRIENDS_COUNT);
    }
    
    public Boolean getGeoEnabled() {
        return (Boolean) getAttribute(TwitterAttributesDefinition.GEO_ENABLED);
    }
    
    public Boolean getIsTranslator() {
        return (Boolean) getAttribute(TwitterAttributesDefinition.IS_TRANSLATOR);
    }
    
    public Integer getListedCount() {
        return (Integer) getAttribute(TwitterAttributesDefinition.LISTED_COUNT);
    }
    
    public Boolean getNotifications() {
        return (Boolean) getAttribute(TwitterAttributesDefinition.NOTIFICATIONS);
    }
    
    public Color getProfileBackgroundColor() {
        return (Color) getAttribute(TwitterAttributesDefinition.PROFILE_BACKGROUND_COLOR);
    }
    
    public String getProfileBackgroundImageUrl() {
        return (String) getAttribute(TwitterAttributesDefinition.PROFILE_BACKGROUND_IMAGE_URL);
    }
    
    public String getProfileBackgroundImageUrlHttps() {
        return (String) getAttribute(TwitterAttributesDefinition.PROFILE_BACKGROUND_IMAGE_URL_HTTPS);
    }
    
    public Boolean getProfileBackgroundTile() {
        return (Boolean) getAttribute(TwitterAttributesDefinition.PROFILE_BACKGROUND_TILE);
    }
    
    public String getProfileImageUrlHttps() {
        return (String) getAttribute(TwitterAttributesDefinition.PROFILE_IMAGE_URL_HTTPS);
    }
    
    public Color getProfileLinkColor() {
        return (Color) getAttribute(TwitterAttributesDefinition.PROFILE_LINK_COLOR);
    }
    
    public Color getProfileSidebarBorderColor() {
        return (Color) getAttribute(TwitterAttributesDefinition.PROFILE_SIDEBAR_BORDER_COLOR);
    }
    
    public Color getProfileSidebarFillColor() {
        return (Color) getAttribute(TwitterAttributesDefinition.PROFILE_SIDEBAR_FILL_COLOR);
    }
    
    public Color getProfileTextColor() {
        return (Color) getAttribute(TwitterAttributesDefinition.PROFILE_TEXT_COLOR);
    }
    
    public Boolean getProfileUseBackgroundImage() {
        return (Boolean) getAttribute(TwitterAttributesDefinition.PROFILE_USE_BACKGROUND_IMAGE);
    }
    
    public Boolean getProtected() {
        return (Boolean) getAttribute(TwitterAttributesDefinition.PROTECTED);
    }
    
    public Boolean getShowAllInlineMedia() {
        return (Boolean) getAttribute(TwitterAttributesDefinition.SHOW_ALL_INLINE_MEDIA);
    }
    
    public Integer getStatusesCount() {
        return (Integer) getAttribute(TwitterAttributesDefinition.STATUSES_COUNT);
    }
    
    public String getTimeZone() {
        return (String) getAttribute(TwitterAttributesDefinition.TIME_ZONE);
    }
    
    public Integer getUtcOffset() {
        return (Integer) getAttribute(TwitterAttributesDefinition.UTC_OFFSET);
    }
    
    public Boolean getVerified() {
        return (Boolean) getAttribute(TwitterAttributesDefinition.VERIFIED);
    }
}
