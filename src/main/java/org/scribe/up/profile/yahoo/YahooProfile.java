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
package org.scribe.up.profile.yahoo;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.scribe.up.profile.Gender;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.util.ObjectHelper;

/**
 * This class is the user profile for Yahoo with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class YahooProfile extends UserProfile {
    
    public static final String PROFILE = "profile";
    public static final String ABOUT_ME = "aboutMe";
    public static final String ADDRESSES = "addresses";
    public static final String BIRTH_YEAR = "birthYear";
    public static final String BIRTHDATE = "birthdate";
    public static final String CREATED = "created";
    public static final String DISPLAY_AGE = "displayAge";
    public static final String DISCLOSURES = "disclosures";
    public static final String EMAILS = "emails";
    public static final String FAMILY_NAME = "familyName";
    public static final String GENDER = "gender";
    public static final String GIVEN_NAME = "givenName";
    public static final String GUID = "guid";
    public static final String IMAGE = "image";
    public static final String INTERESTS = "interests";
    public static final String IS_CONNECTED = "isConnected";
    public static final String LANG = "lang";
    public static final String LOCATION = "location";
    public static final String MEMBER_SINCE = "memberSince";
    public static final String NICKNAME = "nickname";
    public static final String PROFILE_URL = "profileUrl";
    public static final String TIME_ZONE = "timeZone";
    public static final String UPDATED = "updated";
    public static final String URI = "uri";
    
    public YahooProfile() {
        super();
    }
    
    public YahooProfile(String id) {
        super(id);
    }
    
    public YahooProfile(String id, Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getAboutMe() {
        return (String) attributes.get(ABOUT_ME);
    }
    
    public List<YahooAddress> getAddresses() {
        return (List<YahooAddress>) attributes.get(ADDRESSES);
    }
    
    public int getBirthYear() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(BIRTH_YEAR), new Integer(0));
    }
    
    public Date getBirthdate() {
        return (Date) attributes.get(BIRTHDATE);
    }
    
    public Date getCreated() {
        return (Date) attributes.get(CREATED);
    }
    
    public int getDisplayAge() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(DISPLAY_AGE), new Integer(0));
    }
    
    public List<YahooDisclosure> getDisclosures() {
        return (List<YahooDisclosure>) attributes.get(DISCLOSURES);
    }
    
    public List<YahooEmail> getEmails() {
        return (List<YahooEmail>) attributes.get(EMAILS);
    }
    
    public String getFamilyName() {
        return (String) attributes.get(FAMILY_NAME);
    }
    
    public Gender getGender() {
        return (Gender) attributes.get(GENDER);
    }
    
    public String getGivenName() {
        return (String) attributes.get(GIVEN_NAME);
    }
    
    public YahooImage getImage() {
        return (YahooImage) attributes.get(IMAGE);
    }
    
    public List<YahooInterest> getInterests() {
        return (List<YahooInterest>) attributes.get(INTERESTS);
    }
    
    public boolean isConnected() {
        return (Boolean) ObjectHelper.getDefaultIfNull(attributes.get(IS_CONNECTED), Boolean.FALSE);
    }
    
    public Locale getLang() {
        return (Locale) attributes.get(LANG);
    }
    
    public String getLocation() {
        return (String) attributes.get(LOCATION);
    }
    
    public Date getMemberSince() {
        return (Date) attributes.get(MEMBER_SINCE);
    }
    
    public String getNickname() {
        return (String) attributes.get(NICKNAME);
    }
    
    public String getProfileUrl() {
        return (String) attributes.get(PROFILE_URL);
    }
    
    public String getTimeZone() {
        return (String) attributes.get(TIME_ZONE);
    }
    
    public Date getUpdated() {
        return (Date) attributes.get(UPDATED);
    }
    
    public String getUri() {
        return (String) attributes.get(URI);
    }
}
