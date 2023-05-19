package org.pac4j.oauth.profile.facebook;

import lombok.val;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;

import java.io.Serial;
import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * <p>This class is the user profile for Facebook with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.FacebookClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class FacebookProfile extends OAuth20Profile {

    @Serial
    private static final long serialVersionUID = 6339376303764855109L;

    /** {@inheritDoc} */
    @Override
    public String getFamilyName() {
        return (String) getAttribute(FacebookProfileDefinition.LAST_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return (String) getAttribute(FacebookProfileDefinition.NAME);
    }

    /** {@inheritDoc} */
    @Override
    public URI getPictureUrl() {
        val picture = (FacebookPicture) getAttribute(FacebookProfileDefinition.PICTURE);
        if (picture != null) {
            return CommonHelper.asURI(picture.getUrl());
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(FacebookProfileDefinition.LINK);
    }

    /** {@inheritDoc} */
    @Override
    public String getLocation() {
        val location = (FacebookObject) getAttribute(FacebookProfileDefinition.LOCATION);
        if (location != null) {
            return location.getName();
        }
        return null;
    }

    /**
     * <p>getMiddleName.</p>
     *
     * @return a {@link String} object
     */
    public String getMiddleName() {
        return (String) getAttribute(FacebookProfileDefinition.MIDDLE_NAME);
    }

    /**
     * <p>getLanguages.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookObject> getLanguages() {
        return (List<FacebookObject>) getAttribute(FacebookProfileDefinition.LANGUAGES);
    }

    /**
     * <p>getThirdPartyId.</p>
     *
     * @return a {@link String} object
     */
    public String getThirdPartyId() {
        return (String) getAttribute(FacebookProfileDefinition.THIRD_PARTY_ID);
    }

    /**
     * <p>getTimezone.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getTimezone() {
        return (Integer) getAttribute(FacebookProfileDefinition.TIMEZONE);
    }

    /**
     * <p>getUpdateTime.</p>
     *
     * @return a {@link Date} object
     */
    public Date getUpdateTime() {
        return (Date) getAttribute(FacebookProfileDefinition.UPDATED_TIME);
    }

    /**
     * <p>getVerified.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getVerified() {
        return (Boolean) getAttribute(FacebookProfileDefinition.VERIFIED);
    }

    /**
     * <p>getAbout.</p>
     *
     * @return a {@link String} object
     */
    public String getAbout() {
        return (String) getAttribute(FacebookProfileDefinition.ABOUT);
    }

    /**
     * <p>getBirthday.</p>
     *
     * @return a {@link Date} object
     */
    public Date getBirthday() {
        return (Date) getAttribute(FacebookProfileDefinition.BIRTHDAY);
    }

    /**
     * <p>getEducation.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookEducation> getEducation() {
        return (List<FacebookEducation>) getAttribute(FacebookProfileDefinition.EDUCATION);
    }

    /**
     * <p>getHometown.</p>
     *
     * @return a {@link FacebookObject} object
     */
    public FacebookObject getHometown() {
        return (FacebookObject) getAttribute(FacebookProfileDefinition.HOMETOWN);
    }

    /**
     * <p>getInterestedIn.</p>
     *
     * @return a {@link List} object
     */
    public List<String> getInterestedIn() {
        return (List<String>) getAttribute(FacebookProfileDefinition.INTERESTED_IN);
    }

    /**
     * <p>getLocationObject.</p>
     *
     * @return a {@link FacebookObject} object
     */
    public FacebookObject getLocationObject() {
        return (FacebookObject) getAttribute(FacebookProfileDefinition.LOCATION);
    }

    /**
     * <p>getPolitical.</p>
     *
     * @return a {@link String} object
     */
    public String getPolitical() {
        return (String) getAttribute(FacebookProfileDefinition.POLITICAL);
    }

    /**
     * <p>getFavoriteAthletes.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookObject> getFavoriteAthletes() {
        return (List<FacebookObject>) getAttribute(FacebookProfileDefinition.FAVORITE_ATHLETES);
    }

    /**
     * <p>getFavoriteTeams.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookObject> getFavoriteTeams() {
        return (List<FacebookObject>) getAttribute(FacebookProfileDefinition.FAVORITE_TEAMS);
    }

    /**
     * <p>getQuotes.</p>
     *
     * @return a {@link String} object
     */
    public String getQuotes() {
        return (String) getAttribute(FacebookProfileDefinition.QUOTES);
    }

    /**
     * <p>getRelationshipStatus.</p>
     *
     * @return a {@link FacebookRelationshipStatus} object
     */
    public FacebookRelationshipStatus getRelationshipStatus() {
        return (FacebookRelationshipStatus) getAttribute(FacebookProfileDefinition.RELATIONSHIP_STATUS);
    }

    /**
     * <p>getReligion.</p>
     *
     * @return a {@link String} object
     */
    public String getReligion() {
        return (String) getAttribute(FacebookProfileDefinition.RELIGION);
    }

    /**
     * <p>getSignificantOther.</p>
     *
     * @return a {@link FacebookObject} object
     */
    public FacebookObject getSignificantOther() {
        return (FacebookObject) getAttribute(FacebookProfileDefinition.SIGNIFICANT_OTHER);
    }

    /**
     * <p>getWebsite.</p>
     *
     * @return a {@link String} object
     */
    public String getWebsite() {
        return (String) getAttribute(FacebookProfileDefinition.WEBSITE);
    }

    /**
     * <p>getWork.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookWork> getWork() {
        return (List<FacebookWork>) getAttribute(FacebookProfileDefinition.WORK);
    }

    /**
     * <p>getFriends.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookObject> getFriends() {
        return (List<FacebookObject>) getAttribute(FacebookProfileDefinition.FRIENDS);
    }

    /**
     * <p>getMovies.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookInfo> getMovies() {
        return (List<FacebookInfo>) getAttribute(FacebookProfileDefinition.MOVIES);
    }

    /**
     * <p>getMusic.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookInfo> getMusic() {
        return (List<FacebookInfo>) getAttribute(FacebookProfileDefinition.MUSIC);
    }

    /**
     * <p>getBooks.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookInfo> getBooks() {
        return (List<FacebookInfo>) getAttribute(FacebookProfileDefinition.BOOKS);
    }

    /**
     * <p>getLikes.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookInfo> getLikes() {
        return (List<FacebookInfo>) getAttribute(FacebookProfileDefinition.LIKES);
    }

    /**
     * <p>getAlbums.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookPhoto> getAlbums() {
        return (List<FacebookPhoto>) getAttribute(FacebookProfileDefinition.ALBUMS);
    }

    /**
     * <p>getEvents.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookEvent> getEvents() {
        return (List<FacebookEvent>) getAttribute(FacebookProfileDefinition.EVENTS);
    }

    /**
     * <p>getGroups.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookGroup> getGroups() {
        return (List<FacebookGroup>) getAttribute(FacebookProfileDefinition.GROUPS);
    }

    /**
     * <p>getMusicListens.</p>
     *
     * @return a {@link List} object
     */
    public List<FacebookMusicListen> getMusicListens() {
        return (List<FacebookMusicListen>) getAttribute(FacebookProfileDefinition.MUSIC_LISTENS);
    }

    /**
     * <p>getPicture.</p>
     *
     * @return a {@link FacebookPicture} object
     */
    public FacebookPicture getPicture() {
        return (FacebookPicture) getAttribute(FacebookProfileDefinition.PICTURE);
    }
}
