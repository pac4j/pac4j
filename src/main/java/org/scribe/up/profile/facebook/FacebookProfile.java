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
    
    private static final long serialVersionUID = -2666839423218971934L;
    
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
        return (String) attributes.get(FacebookAttributesDefinition.NAME);
    }
    
    public String getFirstName() {
        return (String) attributes.get(FacebookAttributesDefinition.FIRST_NAME);
    }
    
    public String getMiddleName() {
        return (String) attributes.get(FacebookAttributesDefinition.MIDDLE_NAME);
    }
    
    public String getLastName() {
        return (String) attributes.get(FacebookAttributesDefinition.LAST_NAME);
    }
    
    public Gender getGender() {
        return (Gender) attributes.get(FacebookAttributesDefinition.GENDER);
    }
    
    public Locale getLocale() {
        return (Locale) attributes.get(FacebookAttributesDefinition.LOCALE);
    }
    
    public List<FacebookObject> getLanguages() {
        return (List<FacebookObject>) attributes.get(FacebookAttributesDefinition.LANGUAGES);
    }
    
    public String getLink() {
        return (String) attributes.get(FacebookAttributesDefinition.LINK);
    }
    
    public String getUsername() {
        return (String) attributes.get(FacebookAttributesDefinition.USERNAME);
    }
    
    public String getThirdPartyId() {
        return (String) attributes.get(FacebookAttributesDefinition.THIRD_PARTY_ID);
    }
    
    public int getTimezone() {
        return getSafeInt((Integer) attributes.get(FacebookAttributesDefinition.TIMEZONE));
    }
    
    /**
     * Indicate if the timezone attribute exists.
     * 
     * @return if the timezone attribute exists
     */
    public boolean isTimezoneDefined() {
        return attributes.get(FacebookAttributesDefinition.TIMEZONE) != null;
    }
    
    public Date getUpdateTime() {
        return (Date) attributes.get(FacebookAttributesDefinition.UPDATED_TIME);
    }
    
    public boolean isVerified() {
        return getSafeBoolean((Boolean) attributes.get(FacebookAttributesDefinition.VERIFIED));
    }
    
    /**
     * Indicate if the verified attribute exists.
     * 
     * @return if the verified attribute exists
     */
    public boolean isVerifiedDefined() {
        return attributes.get(FacebookAttributesDefinition.VERIFIED) != null;
    }
    
    public String getBio() {
        return (String) attributes.get(FacebookAttributesDefinition.BIO);
    }
    
    public Date getBirthday() {
        return (Date) attributes.get(FacebookAttributesDefinition.BIRTHDAY);
    }
    
    public List<FacebookEducation> getEducation() {
        return (List<FacebookEducation>) attributes.get(FacebookAttributesDefinition.EDUCATION);
    }
    
    public String getEmail() {
        return (String) attributes.get(FacebookAttributesDefinition.EMAIL);
    }
    
    public FacebookObject getHometown() {
        return (FacebookObject) attributes.get(FacebookAttributesDefinition.HOMETOWN);
    }
    
    public List<String> getInterestedIn() {
        return (List<String>) attributes.get(FacebookAttributesDefinition.INTERESTED_IN);
    }
    
    public FacebookObject getLocation() {
        return (FacebookObject) attributes.get(FacebookAttributesDefinition.LOCATION);
    }
    
    public String getPolitical() {
        return (String) attributes.get(FacebookAttributesDefinition.POLITICAL);
    }
    
    public List<FacebookObject> getFavoriteAthletes() {
        return (List<FacebookObject>) attributes.get(FacebookAttributesDefinition.FAVORITE_ATHLETES);
    }
    
    public List<FacebookObject> getFavoriteTeams() {
        return (List<FacebookObject>) attributes.get(FacebookAttributesDefinition.FAVORITE_TEAMS);
    }
    
    public String getQuotes() {
        return (String) attributes.get(FacebookAttributesDefinition.QUOTES);
    }
    
    public FacebookRelationshipStatus getRelationshipStatus() {
        return (FacebookRelationshipStatus) attributes.get(FacebookAttributesDefinition.RELATIONSHIP_STATUS);
    }
    
    public String getReligion() {
        return (String) attributes.get(FacebookAttributesDefinition.RELIGION);
    }
    
    public FacebookObject getSignificantOther() {
        return (FacebookObject) attributes.get(FacebookAttributesDefinition.SIGNIFICANT_OTHER);
    }
    
    public String getWebsite() {
        return (String) attributes.get(FacebookAttributesDefinition.WEBSITE);
    }
    
    public List<FacebookWork> getWork() {
        return (List<FacebookWork>) attributes.get(FacebookAttributesDefinition.WORK);
    }
    
    public List<FacebookObject> getFriends() {
        return (List<FacebookObject>) attributes.get(FacebookAttributesDefinition.FRIENDS);
    }
    
    public List<FacebookInfo> getMovies() {
        return (List<FacebookInfo>) attributes.get(FacebookAttributesDefinition.MOVIES);
    }
    
    public List<FacebookInfo> getMusic() {
        return (List<FacebookInfo>) attributes.get(FacebookAttributesDefinition.MUSIC);
    }
    
    public List<FacebookInfo> getBooks() {
        return (List<FacebookInfo>) attributes.get(FacebookAttributesDefinition.BOOKS);
    }
    
    public List<FacebookInfo> getLikes() {
        return (List<FacebookInfo>) attributes.get(FacebookAttributesDefinition.LIKES);
    }
    
    public List<FacebookPhoto> getAlbums() {
        return (List<FacebookPhoto>) attributes.get(FacebookAttributesDefinition.ALBUMS);
    }
    
    public List<FacebookEvent> getEvents() {
        return (List<FacebookEvent>) attributes.get(FacebookAttributesDefinition.EVENTS);
    }
    
    public List<FacebookGroup> getGroups() {
        return (List<FacebookGroup>) attributes.get(FacebookAttributesDefinition.GROUPS);
    }
}
