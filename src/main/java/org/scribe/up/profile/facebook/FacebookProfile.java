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
import org.scribe.up.provider.impl.FacebookProvider;

/**
 * This class is the user profile for Facebook with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class FacebookProfile extends UserProfile {
    
    private static final long serialVersionUID = 1034405039044925842L;
    
    static {
        definition = new FacebookProfileDefinition();
        providerType = FacebookProvider.TYPE;
    }
    
    public FacebookProfile() {
        super();
    }
    
    public FacebookProfile(Object id) {
        super(id);
    }
    
    public FacebookProfile(Object id, Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getName() {
        return (String) attributes.get(FacebookProfileDefinition.NAME);
    }
    
    public String getFirstName() {
        return (String) attributes.get(FacebookProfileDefinition.FIRST_NAME);
    }
    
    public String getMiddleName() {
        return (String) attributes.get(FacebookProfileDefinition.MIDDLE_NAME);
    }
    
    public String getLastName() {
        return (String) attributes.get(FacebookProfileDefinition.LAST_NAME);
    }
    
    public Gender getGender() {
        return (Gender) attributes.get(FacebookProfileDefinition.GENDER);
    }
    
    public Locale getLocale() {
        return (Locale) attributes.get(FacebookProfileDefinition.LOCALE);
    }
    
    public List<FacebookObject> getLanguages() {
        return (List<FacebookObject>) attributes.get(FacebookProfileDefinition.LANGUAGES);
    }
    
    public String getLink() {
        return (String) attributes.get(FacebookProfileDefinition.LINK);
    }
    
    public String getUsername() {
        return (String) attributes.get(FacebookProfileDefinition.USERNAME);
    }
    
    public String getThirdPartyId() {
        return (String) attributes.get(FacebookProfileDefinition.THIRD_PARTY_ID);
    }
    
    public int getTimezone() {
        return getSafeInteger((Integer) attributes.get(FacebookProfileDefinition.TIMEZONE));
    }
    
    public boolean isTimezoneDefined() {
        return attributes.get(FacebookProfileDefinition.TIMEZONE) != null;
    }
    
    public Date getUpdateTime() {
        return (Date) attributes.get(FacebookProfileDefinition.UPDATED_TIME);
    }
    
    public boolean isVerified() {
        return getSafeBoolean((Boolean) attributes.get(FacebookProfileDefinition.VERIFIED));
    }
    
    public boolean isVerifiedDefined() {
        return attributes.get(FacebookProfileDefinition.VERIFIED) != null;
    }
    
    public String getBio() {
        return (String) attributes.get(FacebookProfileDefinition.BIO);
    }
    
    public Date getBirthday() {
        return (Date) attributes.get(FacebookProfileDefinition.BIRTHDAY);
    }
    
    public List<FacebookEducation> getEducation() {
        return (List<FacebookEducation>) attributes.get(FacebookProfileDefinition.EDUCATION);
    }
    
    public String getEmail() {
        return (String) attributes.get(FacebookProfileDefinition.EMAIL);
    }
    
    public FacebookObject getHometown() {
        return (FacebookObject) attributes.get(FacebookProfileDefinition.HOMETOWN);
    }
    
    public List<String> getInterestedIn() {
        return (List<String>) attributes.get(FacebookProfileDefinition.INTERESTED_IN);
    }
    
    public FacebookObject getLocation() {
        return (FacebookObject) attributes.get(FacebookProfileDefinition.LOCATION);
    }
    
    public String getPolitical() {
        return (String) attributes.get(FacebookProfileDefinition.POLITICAL);
    }
    
    public List<FacebookObject> getFavoriteAthletes() {
        return (List<FacebookObject>) attributes.get(FacebookProfileDefinition.FAVORITE_ATHLETES);
    }
    
    public List<FacebookObject> getFavoriteTeams() {
        return (List<FacebookObject>) attributes.get(FacebookProfileDefinition.FAVORITE_TEAMS);
    }
    
    public String getQuotes() {
        return (String) attributes.get(FacebookProfileDefinition.QUOTES);
    }
    
    public FacebookRelationshipStatus getRelationshipStatus() {
        return (FacebookRelationshipStatus) attributes.get(FacebookProfileDefinition.RELATIONSHIP_STATUS);
    }
    
    public String getReligion() {
        return (String) attributes.get(FacebookProfileDefinition.RELIGION);
    }
    
    public FacebookObject getSignificantOther() {
        return (FacebookObject) attributes.get(FacebookProfileDefinition.SIGNIFICANT_OTHER);
    }
    
    public String getWebsite() {
        return (String) attributes.get(FacebookProfileDefinition.WEBSITE);
    }
    
    public List<FacebookWork> getWork() {
        return (List<FacebookWork>) attributes.get(FacebookProfileDefinition.WORK);
    }
}
