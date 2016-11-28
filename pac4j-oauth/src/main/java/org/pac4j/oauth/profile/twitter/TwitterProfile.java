package org.pac4j.oauth.profile.twitter;

import java.net.URI;
import java.util.Date;
import java.util.Locale;

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

    @Override
    public String getDisplayName() {
        return (String) getAttribute(TwitterProfileDefinition.NAME);
    }
    
    @Override
    public String getUsername() {
        return (String) getAttribute(TwitterProfileDefinition.SCREEN_NAME);
    }
    
    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(TwitterProfileDefinition.LANG);
    }
    
    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(TwitterProfileDefinition.PROFILE_IMAGE_URL);
    }
    
    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(TwitterProfileDefinition.URL);
    }
    
    public Boolean getContributorsEnabled() {
        return (Boolean) getAttribute(TwitterProfileDefinition.CONTRIBUTORS_ENABLED);
    }
    
    public Date getCreatedAt() {
        return (Date) getAttribute(TwitterProfileDefinition.CREATED_AT);
    }
    
    public Boolean getDefaultProfile() {
        return (Boolean) getAttribute(TwitterProfileDefinition.DEFAULT_PROFILE);
    }
    
    public Boolean getDefaultProfileImage() {
        return (Boolean) getAttribute(TwitterProfileDefinition.DEFAULT_PROFILE_IMAGE);
    }
    
    public String getDescription() {
        return (String) getAttribute(TwitterProfileDefinition.DESCRIPTION);
    }
    
    public Integer getFavouritesCount() {
        return (Integer) getAttribute(TwitterProfileDefinition.FAVOURITES_COUNT);
    }
    
    public Boolean getFollowRequestSent() {
        return (Boolean) getAttribute(TwitterProfileDefinition.FOLLOW_REQUEST_SENT);
    }
    
    public Integer getFollowersCount() {
        return (Integer) getAttribute(TwitterProfileDefinition.FOLLOWERS_COUNT);
    }
    
    public Boolean getFollowing() {
        return (Boolean) getAttribute(TwitterProfileDefinition.FOLLOWING);
    }
    
    public Integer getFriendsCount() {
        return (Integer) getAttribute(TwitterProfileDefinition.FRIENDS_COUNT);
    }
    
    public Boolean getGeoEnabled() {
        return (Boolean) getAttribute(TwitterProfileDefinition.GEO_ENABLED);
    }
    
    public Boolean getIsTranslator() {
        return (Boolean) getAttribute(TwitterProfileDefinition.IS_TRANSLATOR);
    }
    
    public Integer getListedCount() {
        return (Integer) getAttribute(TwitterProfileDefinition.LISTED_COUNT);
    }
    
    public Boolean getNotifications() {
        return (Boolean) getAttribute(TwitterProfileDefinition.NOTIFICATIONS);
    }
    
    public Color getProfileBackgroundColor() {
        return (Color) getAttribute(TwitterProfileDefinition.PROFILE_BACKGROUND_COLOR);
    }
    
    public URI getProfileBackgroundImageUrl() {
        return (URI) getAttribute(TwitterProfileDefinition.PROFILE_BACKGROUND_IMAGE_URL);
    }
    
    public URI getProfileBackgroundImageUrlHttps() {
        return (URI) getAttribute(TwitterProfileDefinition.PROFILE_BACKGROUND_IMAGE_URL_HTTPS);
    }
    
    public Boolean getProfileBackgroundTile() {
        return (Boolean) getAttribute(TwitterProfileDefinition.PROFILE_BACKGROUND_TILE);
    }
    
    public URI getProfileImageUrlHttps() {
        return (URI) getAttribute(TwitterProfileDefinition.PROFILE_IMAGE_URL_HTTPS);
    }
    
    public Color getProfileLinkColor() {
        return (Color) getAttribute(TwitterProfileDefinition.PROFILE_LINK_COLOR);
    }
    
    public Color getProfileSidebarBorderColor() {
        return (Color) getAttribute(TwitterProfileDefinition.PROFILE_SIDEBAR_BORDER_COLOR);
    }
    
    public Color getProfileSidebarFillColor() {
        return (Color) getAttribute(TwitterProfileDefinition.PROFILE_SIDEBAR_FILL_COLOR);
    }
    
    public Color getProfileTextColor() {
        return (Color) getAttribute(TwitterProfileDefinition.PROFILE_TEXT_COLOR);
    }
    
    public Boolean getProfileUseBackgroundImage() {
        return (Boolean) getAttribute(TwitterProfileDefinition.PROFILE_USE_BACKGROUND_IMAGE);
    }
    
    public Boolean getProtected() {
        return (Boolean) getAttribute(TwitterProfileDefinition.PROTECTED);
    }
    
    public Boolean getShowAllInlineMedia() {
        return (Boolean) getAttribute(TwitterProfileDefinition.SHOW_ALL_INLINE_MEDIA);
    }
    
    public Integer getStatusesCount() {
        return (Integer) getAttribute(TwitterProfileDefinition.STATUSES_COUNT);
    }
    
    public String getTimeZone() {
        return (String) getAttribute(TwitterProfileDefinition.TIME_ZONE);
    }
    
    public Integer getUtcOffset() {
        return (Integer) getAttribute(TwitterProfileDefinition.UTC_OFFSET);
    }
    
    public Boolean getVerified() {
        return (Boolean) getAttribute(TwitterProfileDefinition.VERIFIED);
    }
}
