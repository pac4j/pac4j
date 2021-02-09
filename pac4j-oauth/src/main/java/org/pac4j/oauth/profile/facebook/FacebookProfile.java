package org.pac4j.oauth.profile.facebook;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for Facebook with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.FacebookClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class FacebookProfile extends OAuth20Profile {

    private static final long serialVersionUID = 6339376303764855109L;

    @Override
    public String getFamilyName() {
        return (String) getAttribute(FacebookProfileDefinition.LAST_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(FacebookProfileDefinition.NAME);
    }

    @Override
    public URI getPictureUrl() {
        final var picture = (FacebookPicture) getAttribute(FacebookProfileDefinition.PICTURE);
        if (picture != null) {
            return CommonHelper.asURI(picture.getUrl());
        }
        return null;
    }

    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(FacebookProfileDefinition.LINK);
    }

    @Override
    public String getLocation() {
        final var location = (FacebookObject) getAttribute(FacebookProfileDefinition.LOCATION);
        if (location != null) {
            return location.getName();
        }
        return null;
    }

    public String getMiddleName() {
        return (String) getAttribute(FacebookProfileDefinition.MIDDLE_NAME);
    }

    public List<FacebookObject> getLanguages() {
        return (List<FacebookObject>) getAttribute(FacebookProfileDefinition.LANGUAGES);
    }

    public String getThirdPartyId() {
        return (String) getAttribute(FacebookProfileDefinition.THIRD_PARTY_ID);
    }

    public Integer getTimezone() {
        return (Integer) getAttribute(FacebookProfileDefinition.TIMEZONE);
    }

    public Date getUpdateTime() {
        return (Date) getAttribute(FacebookProfileDefinition.UPDATED_TIME);
    }

    public Boolean getVerified() {
        return (Boolean) getAttribute(FacebookProfileDefinition.VERIFIED);
    }

    public String getAbout() {
        return (String) getAttribute(FacebookProfileDefinition.ABOUT);
    }

    public Date getBirthday() {
        return (Date) getAttribute(FacebookProfileDefinition.BIRTHDAY);
    }

    public List<FacebookEducation> getEducation() {
        return (List<FacebookEducation>) getAttribute(FacebookProfileDefinition.EDUCATION);
    }

    public FacebookObject getHometown() {
        return (FacebookObject) getAttribute(FacebookProfileDefinition.HOMETOWN);
    }

    public List<String> getInterestedIn() {
        return (List<String>) getAttribute(FacebookProfileDefinition.INTERESTED_IN);
    }

    public FacebookObject getLocationObject() {
        return (FacebookObject) getAttribute(FacebookProfileDefinition.LOCATION);
    }

    public String getPolitical() {
        return (String) getAttribute(FacebookProfileDefinition.POLITICAL);
    }

    public List<FacebookObject> getFavoriteAthletes() {
        return (List<FacebookObject>) getAttribute(FacebookProfileDefinition.FAVORITE_ATHLETES);
    }

    public List<FacebookObject> getFavoriteTeams() {
        return (List<FacebookObject>) getAttribute(FacebookProfileDefinition.FAVORITE_TEAMS);
    }

    public String getQuotes() {
        return (String) getAttribute(FacebookProfileDefinition.QUOTES);
    }

    public FacebookRelationshipStatus getRelationshipStatus() {
        return (FacebookRelationshipStatus) getAttribute(FacebookProfileDefinition.RELATIONSHIP_STATUS);
    }

    public String getReligion() {
        return (String) getAttribute(FacebookProfileDefinition.RELIGION);
    }

    public FacebookObject getSignificantOther() {
        return (FacebookObject) getAttribute(FacebookProfileDefinition.SIGNIFICANT_OTHER);
    }

    public String getWebsite() {
        return (String) getAttribute(FacebookProfileDefinition.WEBSITE);
    }

    public List<FacebookWork> getWork() {
        return (List<FacebookWork>) getAttribute(FacebookProfileDefinition.WORK);
    }

    public List<FacebookObject> getFriends() {
        return (List<FacebookObject>) getAttribute(FacebookProfileDefinition.FRIENDS);
    }

    public List<FacebookInfo> getMovies() {
        return (List<FacebookInfo>) getAttribute(FacebookProfileDefinition.MOVIES);
    }

    public List<FacebookInfo> getMusic() {
        return (List<FacebookInfo>) getAttribute(FacebookProfileDefinition.MUSIC);
    }

    public List<FacebookInfo> getBooks() {
        return (List<FacebookInfo>) getAttribute(FacebookProfileDefinition.BOOKS);
    }

    public List<FacebookInfo> getLikes() {
        return (List<FacebookInfo>) getAttribute(FacebookProfileDefinition.LIKES);
    }

    public List<FacebookPhoto> getAlbums() {
        return (List<FacebookPhoto>) getAttribute(FacebookProfileDefinition.ALBUMS);
    }

    public List<FacebookEvent> getEvents() {
        return (List<FacebookEvent>) getAttribute(FacebookProfileDefinition.EVENTS);
    }

    public List<FacebookGroup> getGroups() {
        return (List<FacebookGroup>) getAttribute(FacebookProfileDefinition.GROUPS);
    }

    public List<FacebookMusicListen> getMusicListens() {
        return (List<FacebookMusicListen>) getAttribute(FacebookProfileDefinition.MUSIC_LISTENS);
    }

    public FacebookPicture getPicture() {
        return (FacebookPicture) getAttribute(FacebookProfileDefinition.PICTURE);
    }
}
