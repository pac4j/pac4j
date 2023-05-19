package org.pac4j.oauth.profile.twitter;

import java.io.Serial;
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

    @Serial
    private static final long serialVersionUID = -6473348745856820192L;

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return (String) getAttribute(TwitterProfileDefinition.NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return (String) getAttribute(TwitterProfileDefinition.SCREEN_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(TwitterProfileDefinition.LANG);
    }

    /** {@inheritDoc} */
    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(TwitterProfileDefinition.PROFILE_IMAGE_URL);
    }

    /** {@inheritDoc} */
    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(TwitterProfileDefinition.URL);
    }

    /**
     * <p>getContributorsEnabled.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getContributorsEnabled() {
        return (Boolean) getAttribute(TwitterProfileDefinition.CONTRIBUTORS_ENABLED);
    }

    /**
     * <p>getCreatedAt.</p>
     *
     * @return a {@link Date} object
     */
    public Date getCreatedAt() {
        return (Date) getAttribute(TwitterProfileDefinition.CREATED_AT);
    }

    /**
     * <p>getDefaultProfile.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getDefaultProfile() {
        return (Boolean) getAttribute(TwitterProfileDefinition.DEFAULT_PROFILE);
    }

    /**
     * <p>getDefaultProfileImage.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getDefaultProfileImage() {
        return (Boolean) getAttribute(TwitterProfileDefinition.DEFAULT_PROFILE_IMAGE);
    }

    /**
     * <p>getDescription.</p>
     *
     * @return a {@link String} object
     */
    public String getDescription() {
        return (String) getAttribute(TwitterProfileDefinition.DESCRIPTION);
    }

    /**
     * <p>getFavouritesCount.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getFavouritesCount() {
        return (Integer) getAttribute(TwitterProfileDefinition.FAVOURITES_COUNT);
    }

    /**
     * <p>getFollowRequestSent.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getFollowRequestSent() {
        return (Boolean) getAttribute(TwitterProfileDefinition.FOLLOW_REQUEST_SENT);
    }

    /**
     * <p>getFollowersCount.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getFollowersCount() {
        return (Integer) getAttribute(TwitterProfileDefinition.FOLLOWERS_COUNT);
    }

    /**
     * <p>getFollowing.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getFollowing() {
        return (Boolean) getAttribute(TwitterProfileDefinition.FOLLOWING);
    }

    /**
     * <p>getFriendsCount.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getFriendsCount() {
        return (Integer) getAttribute(TwitterProfileDefinition.FRIENDS_COUNT);
    }

    /**
     * <p>getGeoEnabled.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getGeoEnabled() {
        return (Boolean) getAttribute(TwitterProfileDefinition.GEO_ENABLED);
    }

    /**
     * <p>getIsTranslator.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getIsTranslator() {
        return (Boolean) getAttribute(TwitterProfileDefinition.IS_TRANSLATOR);
    }

    /**
     * <p>getListedCount.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getListedCount() {
        return (Integer) getAttribute(TwitterProfileDefinition.LISTED_COUNT);
    }

    /**
     * <p>getNotifications.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getNotifications() {
        return (Boolean) getAttribute(TwitterProfileDefinition.NOTIFICATIONS);
    }

    /**
     * <p>getProfileBackgroundColor.</p>
     *
     * @return a {@link Color} object
     */
    public Color getProfileBackgroundColor() {
        return (Color) getAttribute(TwitterProfileDefinition.PROFILE_BACKGROUND_COLOR);
    }

    /**
     * <p>getProfileBackgroundImageUrl.</p>
     *
     * @return a {@link URI} object
     */
    public URI getProfileBackgroundImageUrl() {
        return (URI) getAttribute(TwitterProfileDefinition.PROFILE_BACKGROUND_IMAGE_URL);
    }

    /**
     * <p>getProfileBackgroundImageUrlHttps.</p>
     *
     * @return a {@link URI} object
     */
    public URI getProfileBackgroundImageUrlHttps() {
        return (URI) getAttribute(TwitterProfileDefinition.PROFILE_BACKGROUND_IMAGE_URL_HTTPS);
    }

    /**
     * <p>getProfileBackgroundTile.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getProfileBackgroundTile() {
        return (Boolean) getAttribute(TwitterProfileDefinition.PROFILE_BACKGROUND_TILE);
    }

    /**
     * <p>getProfileImageUrlHttps.</p>
     *
     * @return a {@link URI} object
     */
    public URI getProfileImageUrlHttps() {
        return (URI) getAttribute(TwitterProfileDefinition.PROFILE_IMAGE_URL_HTTPS);
    }

    /**
     * <p>getProfileLinkColor.</p>
     *
     * @return a {@link Color} object
     */
    public Color getProfileLinkColor() {
        return (Color) getAttribute(TwitterProfileDefinition.PROFILE_LINK_COLOR);
    }

    /**
     * <p>getProfileSidebarBorderColor.</p>
     *
     * @return a {@link Color} object
     */
    public Color getProfileSidebarBorderColor() {
        return (Color) getAttribute(TwitterProfileDefinition.PROFILE_SIDEBAR_BORDER_COLOR);
    }

    /**
     * <p>getProfileSidebarFillColor.</p>
     *
     * @return a {@link Color} object
     */
    public Color getProfileSidebarFillColor() {
        return (Color) getAttribute(TwitterProfileDefinition.PROFILE_SIDEBAR_FILL_COLOR);
    }

    /**
     * <p>getProfileTextColor.</p>
     *
     * @return a {@link Color} object
     */
    public Color getProfileTextColor() {
        return (Color) getAttribute(TwitterProfileDefinition.PROFILE_TEXT_COLOR);
    }

    /**
     * <p>getProfileUseBackgroundImage.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getProfileUseBackgroundImage() {
        return (Boolean) getAttribute(TwitterProfileDefinition.PROFILE_USE_BACKGROUND_IMAGE);
    }

    /**
     * <p>getProtected.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getProtected() {
        return (Boolean) getAttribute(TwitterProfileDefinition.PROTECTED);
    }

    /**
     * <p>getShowAllInlineMedia.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getShowAllInlineMedia() {
        return (Boolean) getAttribute(TwitterProfileDefinition.SHOW_ALL_INLINE_MEDIA);
    }

    /**
     * <p>getStatusesCount.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getStatusesCount() {
        return (Integer) getAttribute(TwitterProfileDefinition.STATUSES_COUNT);
    }

    /**
     * <p>getTimeZone.</p>
     *
     * @return a {@link String} object
     */
    public String getTimeZone() {
        return (String) getAttribute(TwitterProfileDefinition.TIME_ZONE);
    }

    /**
     * <p>getUtcOffset.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getUtcOffset() {
        return (Integer) getAttribute(TwitterProfileDefinition.UTC_OFFSET);
    }

    /**
     * <p>getVerified.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getVerified() {
        return (Boolean) getAttribute(TwitterProfileDefinition.VERIFIED);
    }
}
