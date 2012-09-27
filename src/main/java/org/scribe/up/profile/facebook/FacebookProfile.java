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
package org.scribe.up.profile.facebook;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.UserProfile;

/**
 * This class is the user profile for Facebook with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class FacebookProfile extends UserProfile {
    
    private static final long serialVersionUID = -2936087135435951503L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return AttributesDefinitions.facebookDefinition;
    }
    
    public FacebookProfile() {
        super();
    }
    
    public FacebookProfile(final Object id) {
        super(id);
    }
    
    public FacebookProfile(final Object id, final Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getName() {
        return (String) this.attributes.get(FacebookAttributesDefinition.NAME);
    }
    
    public String getFirstName() {
        return (String) this.attributes.get(FacebookAttributesDefinition.FIRST_NAME);
    }
    
    public String getMiddleName() {
        return (String) this.attributes.get(FacebookAttributesDefinition.MIDDLE_NAME);
    }
    
    public String getLastName() {
        return (String) this.attributes.get(FacebookAttributesDefinition.LAST_NAME);
    }
    
    public Gender getGender() {
        return (Gender) this.attributes.get(FacebookAttributesDefinition.GENDER);
    }
    
    public Locale getLocale() {
        return (Locale) this.attributes.get(FacebookAttributesDefinition.LOCALE);
    }
    
    public List<FacebookObject> getLanguages() {
        return (List<FacebookObject>) this.attributes.get(FacebookAttributesDefinition.LANGUAGES);
    }
    
    public String getLink() {
        return (String) this.attributes.get(FacebookAttributesDefinition.LINK);
    }
    
    public String getUsername() {
        return (String) this.attributes.get(FacebookAttributesDefinition.USERNAME);
    }
    
    public String getThirdPartyId() {
        return (String) this.attributes.get(FacebookAttributesDefinition.THIRD_PARTY_ID);
    }
    
    public int getTimezone() {
        return getSafeInt((Integer) this.attributes.get(FacebookAttributesDefinition.TIMEZONE));
    }
    
    /**
     * Indicate if the timezone attribute exists.
     * 
     * @return if the timezone attribute exists
     */
    public boolean isTimezoneDefined() {
        return this.attributes.get(FacebookAttributesDefinition.TIMEZONE) != null;
    }
    
    public Date getUpdateTime() {
        return (Date) this.attributes.get(FacebookAttributesDefinition.UPDATED_TIME);
    }
    
    public boolean isVerified() {
        return getSafeBoolean((Boolean) this.attributes.get(FacebookAttributesDefinition.VERIFIED));
    }
    
    /**
     * Indicate if the verified attribute exists.
     * 
     * @return if the verified attribute exists
     */
    public boolean isVerifiedDefined() {
        return this.attributes.get(FacebookAttributesDefinition.VERIFIED) != null;
    }
    
    public String getBio() {
        return (String) this.attributes.get(FacebookAttributesDefinition.BIO);
    }
    
    public Date getBirthday() {
        return (Date) this.attributes.get(FacebookAttributesDefinition.BIRTHDAY);
    }
    
    public List<FacebookEducation> getEducation() {
        return (List<FacebookEducation>) this.attributes.get(FacebookAttributesDefinition.EDUCATION);
    }
    
    public String getEmail() {
        return (String) this.attributes.get(FacebookAttributesDefinition.EMAIL);
    }
    
    public FacebookObject getHometown() {
        return (FacebookObject) this.attributes.get(FacebookAttributesDefinition.HOMETOWN);
    }
    
    public List<String> getInterestedIn() {
        return (List<String>) this.attributes.get(FacebookAttributesDefinition.INTERESTED_IN);
    }
    
    public FacebookObject getLocation() {
        return (FacebookObject) this.attributes.get(FacebookAttributesDefinition.LOCATION);
    }
    
    public String getPolitical() {
        return (String) this.attributes.get(FacebookAttributesDefinition.POLITICAL);
    }
    
    public List<FacebookObject> getFavoriteAthletes() {
        return (List<FacebookObject>) this.attributes.get(FacebookAttributesDefinition.FAVORITE_ATHLETES);
    }
    
    public List<FacebookObject> getFavoriteTeams() {
        return (List<FacebookObject>) this.attributes.get(FacebookAttributesDefinition.FAVORITE_TEAMS);
    }
    
    public String getQuotes() {
        return (String) this.attributes.get(FacebookAttributesDefinition.QUOTES);
    }
    
    public FacebookRelationshipStatus getRelationshipStatus() {
        return (FacebookRelationshipStatus) this.attributes.get(FacebookAttributesDefinition.RELATIONSHIP_STATUS);
    }
    
    public String getReligion() {
        return (String) this.attributes.get(FacebookAttributesDefinition.RELIGION);
    }
    
    public FacebookObject getSignificantOther() {
        return (FacebookObject) this.attributes.get(FacebookAttributesDefinition.SIGNIFICANT_OTHER);
    }
    
    public String getWebsite() {
        return (String) this.attributes.get(FacebookAttributesDefinition.WEBSITE);
    }
    
    public List<FacebookWork> getWork() {
        return (List<FacebookWork>) this.attributes.get(FacebookAttributesDefinition.WORK);
    }
    
    public List<FacebookObject> getFriends() {
        return (List<FacebookObject>) this.attributes.get(FacebookAttributesDefinition.FRIENDS);
    }
    
    public List<FacebookInfo> getMovies() {
        return (List<FacebookInfo>) this.attributes.get(FacebookAttributesDefinition.MOVIES);
    }
    
    public List<FacebookInfo> getMusic() {
        return (List<FacebookInfo>) this.attributes.get(FacebookAttributesDefinition.MUSIC);
    }
    
    public List<FacebookInfo> getBooks() {
        return (List<FacebookInfo>) this.attributes.get(FacebookAttributesDefinition.BOOKS);
    }
    
    public List<FacebookInfo> getLikes() {
        return (List<FacebookInfo>) this.attributes.get(FacebookAttributesDefinition.LIKES);
    }
    
    public List<FacebookPhoto> getAlbums() {
        return (List<FacebookPhoto>) this.attributes.get(FacebookAttributesDefinition.ALBUMS);
    }
    
    public List<FacebookEvent> getEvents() {
        return (List<FacebookEvent>) this.attributes.get(FacebookAttributesDefinition.EVENTS);
    }
    
    public List<FacebookGroup> getGroups() {
        return (List<FacebookGroup>) this.attributes.get(FacebookAttributesDefinition.GROUPS);
    }
    
    public List<FacebookMusicListen> getMusicListens() {
        return (List<FacebookMusicListen>) this.attributes.get(FacebookAttributesDefinition.MUSIC_LISTENS);
    }
}
