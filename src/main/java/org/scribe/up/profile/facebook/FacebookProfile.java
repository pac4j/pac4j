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

import org.scribe.up.profile.Gender;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.util.ObjectHelper;

/**
 * This class is the user profile for Facebook with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class FacebookProfile extends UserProfile {
    
    public static final String NAME = "name";
    public static final String FIRST_NAME = "first_name";
    public static final String MIDDLE_NAME = "middle_name";
    public static final String LAST_NAME = "last_name";
    public static final String GENDER = "gender";
    public static final String LOCALE = "locale";
    public static final String LANGUAGES = "languages";
    public static final String LINK = "link";
    public static final String USERNAME = "username";
    public static final String THIRD_PARTY_ID = "third_party_id";
    public static final String TIMEZONE = "timezone";
    public static final String UPDATED_TIME = "updated_time";
    public static final String VERIFIED = "verified";
    public static final String BIO = "bio";
    public static final String BIRTHDAY = "birthday";
    public static final String EDUCATION = "education";
    public static final String EMAIL = "email";
    public static final String HOMETOWN = "hometown";
    public static final String INTERESTED_IN = "interested_in";
    public static final String LOCATION = "location";
    public static final String POLITICAL = "political";
    public static final String FAVORITE_ATHLETES = "favorite_athletes";
    public static final String FAVORITE_TEAMS = "favorite_teams";
    public static final String QUOTES = "quotes";
    public static final String RELATIONSHIP_STATUS = "relationship_status";
    public static final String RELIGION = "religion";
    public static final String SIGNIFICANT_OTHER = "significant_other";
    public static final String WEBSITE = "website";
    public static final String WORK = "work";
    
    public FacebookProfile() {
        super();
    }
    
    public FacebookProfile(String id) {
        super(id);
    }
    
    public FacebookProfile(String id, Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getName() {
        return (String) attributes.get(NAME);
    }
    
    public String getFirstName() {
        return (String) attributes.get(FIRST_NAME);
    }
    
    public String getMiddleName() {
        return (String) attributes.get(MIDDLE_NAME);
    }
    
    public String getLastName() {
        return (String) attributes.get(LAST_NAME);
    }
    
    public Gender getGender() {
        return (Gender) attributes.get(GENDER);
    }
    
    public Locale getLocale() {
        return (Locale) attributes.get(LOCALE);
    }
    
    public List<FacebookObject> getLanguages() {
        return (List<FacebookObject>) attributes.get(LANGUAGES);
    }
    
    public String getLink() {
        return (String) attributes.get(LINK);
    }
    
    public String getUsername() {
        return (String) attributes.get(USERNAME);
    }
    
    public String getThirdPartyId() {
        return (String) attributes.get(THIRD_PARTY_ID);
    }
    
    public int getTimezone() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(TIMEZONE), new Integer(0));
    }
    
    public Date getUpdateTime() {
        return (Date) attributes.get(UPDATED_TIME);
    }
    
    public boolean isVerified() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(VERIFIED), Boolean.FALSE);
    }
    
    public String getBio() {
        return (String) attributes.get(BIO);
    }
    
    public Date getBirthday() {
        return (Date) attributes.get(BIRTHDAY);
    }
    
    public List<FacebookEducation> getEducation() {
        return (List<FacebookEducation>) attributes.get(EDUCATION);
    }
    
    public String getEmail() {
        return (String) attributes.get(EMAIL);
    }
    
    public FacebookObject getHometown() {
        return (FacebookObject) attributes.get(HOMETOWN);
    }
    
    public List<String> getInterestedIn() {
        return (List<String>) attributes.get(INTERESTED_IN);
    }
    
    public FacebookObject getLocation() {
        return (FacebookObject) attributes.get(LOCATION);
    }
    
    public String getPolitical() {
        return (String) attributes.get(POLITICAL);
    }
    
    public List<FacebookObject> getFavoriteAthletes() {
        return (List<FacebookObject>) attributes.get(FAVORITE_ATHLETES);
    }
    
    public List<FacebookObject> getFavoriteTeams() {
        return (List<FacebookObject>) attributes.get(FAVORITE_TEAMS);
    }
    
    public String getQuotes() {
        return (String) attributes.get(QUOTES);
    }
    
    public FacebookRelationshipStatus getRelationshipStatus() {
        return (FacebookRelationshipStatus) attributes.get(RELATIONSHIP_STATUS);
    }
    
    public String getReligion() {
        return (String) attributes.get(RELIGION);
    }
    
    public FacebookObject getSignificantOther() {
        return (FacebookObject) attributes.get(SIGNIFICANT_OTHER);
    }
    
    public String getWebsite() {
        return (String) attributes.get(WEBSITE);
    }
    
    public List<FacebookWork> getWork() {
        return (List<FacebookWork>) attributes.get(WORK);
    }
}
