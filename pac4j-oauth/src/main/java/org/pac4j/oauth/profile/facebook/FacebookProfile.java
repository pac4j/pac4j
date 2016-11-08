package org.pac4j.oauth.profile.facebook;

import java.util.Date;
import java.util.List;

import org.pac4j.core.profile.AttributesDefinition;
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

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new FacebookAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(FacebookAttributesDefinition.LAST_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(FacebookAttributesDefinition.NAME);
    }

    @Override
    public String getPictureUrl() {
        final FacebookPicture picture = (FacebookPicture) getAttribute(FacebookAttributesDefinition.PICTURE);
        if (picture != null) {
            return picture.getUrl();
        }
        return null;
    }

    @Override
    public String getProfileUrl() {
        return (String) getAttribute(FacebookAttributesDefinition.LINK);
    }

    @Override
    public String getLocation() {
        final FacebookObject location = (FacebookObject) getAttribute(FacebookAttributesDefinition.LOCATION);
        if (location != null) {
            return location.getName();
        }
        return null;
    }

    public String getMiddleName() {
        return (String) getAttribute(FacebookAttributesDefinition.MIDDLE_NAME);
    }

    public List<FacebookObject> getLanguages() {
        return (List<FacebookObject>) getAttribute(FacebookAttributesDefinition.LANGUAGES);
    }

    public String getThirdPartyId() {
        return (String) getAttribute(FacebookAttributesDefinition.THIRD_PARTY_ID);
    }

    public Integer getTimezone() {
        return (Integer) getAttribute(FacebookAttributesDefinition.TIMEZONE);
    }

    public Date getUpdateTime() {
        return (Date) getAttribute(FacebookAttributesDefinition.UPDATED_TIME);
    }

    public Boolean getVerified() {
        return (Boolean) getAttribute(FacebookAttributesDefinition.VERIFIED);
    }

	/**
	 * The bio field on the User object is no longer available. If the bio field was set for a person,
	 * the value will now be appended to the about field.
	 *
	 * Use {@link #getAbout()} instead
	 */
	@Deprecated
    public String getBio() {
        return (String) getAttribute(FacebookAttributesDefinition.ABOUT);
    }

    public String getAbout() {
        return (String) getAttribute(FacebookAttributesDefinition.ABOUT);
    }

    public Date getBirthday() {
        return (Date) getAttribute(FacebookAttributesDefinition.BIRTHDAY);
    }

    public List<FacebookEducation> getEducation() {
        return (List<FacebookEducation>) getAttribute(FacebookAttributesDefinition.EDUCATION);
    }

    public FacebookObject getHometown() {
        return (FacebookObject) getAttribute(FacebookAttributesDefinition.HOMETOWN);
    }

    public List<String> getInterestedIn() {
        return (List<String>) getAttribute(FacebookAttributesDefinition.INTERESTED_IN);
    }

    public FacebookObject getLocationObject() {
        return (FacebookObject) getAttribute(FacebookAttributesDefinition.LOCATION);
    }

    public String getPolitical() {
        return (String) getAttribute(FacebookAttributesDefinition.POLITICAL);
    }

    public List<FacebookObject> getFavoriteAthletes() {
        return (List<FacebookObject>) getAttribute(FacebookAttributesDefinition.FAVORITE_ATHLETES);
    }

    public List<FacebookObject> getFavoriteTeams() {
        return (List<FacebookObject>) getAttribute(FacebookAttributesDefinition.FAVORITE_TEAMS);
    }

    public String getQuotes() {
        return (String) getAttribute(FacebookAttributesDefinition.QUOTES);
    }

    public FacebookRelationshipStatus getRelationshipStatus() {
        return (FacebookRelationshipStatus) getAttribute(FacebookAttributesDefinition.RELATIONSHIP_STATUS);
    }

    public String getReligion() {
        return (String) getAttribute(FacebookAttributesDefinition.RELIGION);
    }

    public FacebookObject getSignificantOther() {
        return (FacebookObject) getAttribute(FacebookAttributesDefinition.SIGNIFICANT_OTHER);
    }

    public String getWebsite() {
        return (String) getAttribute(FacebookAttributesDefinition.WEBSITE);
    }

    public List<FacebookWork> getWork() {
        return (List<FacebookWork>) getAttribute(FacebookAttributesDefinition.WORK);
    }

    public List<FacebookObject> getFriends() {
        return (List<FacebookObject>) getAttribute(FacebookAttributesDefinition.FRIENDS);
    }

    public List<FacebookInfo> getMovies() {
        return (List<FacebookInfo>) getAttribute(FacebookAttributesDefinition.MOVIES);
    }

    public List<FacebookInfo> getMusic() {
        return (List<FacebookInfo>) getAttribute(FacebookAttributesDefinition.MUSIC);
    }

    public List<FacebookInfo> getBooks() {
        return (List<FacebookInfo>) getAttribute(FacebookAttributesDefinition.BOOKS);
    }

    public List<FacebookInfo> getLikes() {
        return (List<FacebookInfo>) getAttribute(FacebookAttributesDefinition.LIKES);
    }

    public List<FacebookPhoto> getAlbums() {
        return (List<FacebookPhoto>) getAttribute(FacebookAttributesDefinition.ALBUMS);
    }

    public List<FacebookEvent> getEvents() {
        return (List<FacebookEvent>) getAttribute(FacebookAttributesDefinition.EVENTS);
    }

    public List<FacebookGroup> getGroups() {
        return (List<FacebookGroup>) getAttribute(FacebookAttributesDefinition.GROUPS);
    }

    public List<FacebookMusicListen> getMusicListens() {
        return (List<FacebookMusicListen>) getAttribute(FacebookAttributesDefinition.MUSIC_LISTENS);
    }

    public FacebookPicture getPicture() {
        return (FacebookPicture) getAttribute(FacebookAttributesDefinition.PICTURE);
    }
}
