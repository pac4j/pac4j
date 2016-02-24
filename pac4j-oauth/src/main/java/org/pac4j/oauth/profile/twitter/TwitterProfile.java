package org.pac4j.oauth.profile.twitter;

import java.util.Date;
import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.Color;
import org.pac4j.oauth.profile.OAuth10Profile;

/**
 * <p>This class is the user profile for Twitter with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.TwitterClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class TwitterProfile extends OAuth10Profile {
    
    private static final long serialVersionUID = -6473348745856820192L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new TwitterAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
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
