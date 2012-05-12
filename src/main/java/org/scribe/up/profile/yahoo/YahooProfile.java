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

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.UserProfile;

/**
 * This class is the user profile for Yahoo with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class YahooProfile extends UserProfile {
    
    private static final long serialVersionUID = 8396796531584556563L;
    
    protected AttributesDefinition getAttributesDefinition() {
        return AttributesDefinitions.yahooDefinition;
    }
    
    public YahooProfile() {
        super();
    }
    
    public YahooProfile(Object id) {
        super(id);
    }
    
    public YahooProfile(Object id, Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getAboutMe() {
        return (String) attributes.get(YahooAttributesDefinition.ABOUT_ME);
    }
    
    public List<YahooAddress> getAddresses() {
        return (List<YahooAddress>) attributes.get(YahooAttributesDefinition.ADDRESSES);
    }
    
    public int getBirthYear() {
        return getSafeInt((Integer) attributes.get(YahooAttributesDefinition.BIRTH_YEAR));
    }
    
    /**
     * Indicate if the birthYear attribute exists.
     * 
     * @return if the birthYear attribute exists
     */
    public boolean isBirthYearDefined() {
        return attributes.get(YahooAttributesDefinition.BIRTH_YEAR) != null;
    }
    
    public Date getBirthdate() {
        return (Date) attributes.get(YahooAttributesDefinition.BIRTHDATE);
    }
    
    public Date getCreated() {
        return (Date) attributes.get(YahooAttributesDefinition.CREATED);
    }
    
    public int getDisplayAge() {
        return getSafeInt((Integer) attributes.get(YahooAttributesDefinition.DISPLAY_AGE));
    }
    
    /**
     * Indicate if the displayAge attribute exists.
     * 
     * @return if the displayAge attribute exists
     */
    public boolean isDisplayAgeDefined() {
        return attributes.get(YahooAttributesDefinition.DISPLAY_AGE) != null;
    }
    
    public List<YahooDisclosure> getDisclosures() {
        return (List<YahooDisclosure>) attributes.get(YahooAttributesDefinition.DISCLOSURES);
    }
    
    public List<YahooEmail> getEmails() {
        return (List<YahooEmail>) attributes.get(YahooAttributesDefinition.EMAILS);
    }
    
    public String getFamilyName() {
        return (String) attributes.get(YahooAttributesDefinition.FAMILY_NAME);
    }
    
    public Gender getGender() {
        return (Gender) attributes.get(YahooAttributesDefinition.GENDER);
    }
    
    public String getGivenName() {
        return (String) attributes.get(YahooAttributesDefinition.GIVEN_NAME);
    }
    
    public YahooImage getImage() {
        return (YahooImage) attributes.get(YahooAttributesDefinition.IMAGE);
    }
    
    public List<YahooInterest> getInterests() {
        return (List<YahooInterest>) attributes.get(YahooAttributesDefinition.INTERESTS);
    }
    
    public boolean isConnected() {
        return getSafeBoolean((Boolean) attributes.get(YahooAttributesDefinition.IS_CONNECTED));
    }
    
    /**
     * Indicate if the isConnected attribute exists.
     * 
     * @return if the isConnected attribute exists
     */
    public boolean isConnectedDefined() {
        return attributes.get(YahooAttributesDefinition.IS_CONNECTED) != null;
    }
    
    public Locale getLang() {
        return (Locale) attributes.get(YahooAttributesDefinition.LANG);
    }
    
    public String getLocation() {
        return (String) attributes.get(YahooAttributesDefinition.LOCATION);
    }
    
    public Date getMemberSince() {
        return (Date) attributes.get(YahooAttributesDefinition.MEMBER_SINCE);
    }
    
    public String getNickname() {
        return (String) attributes.get(YahooAttributesDefinition.NICKNAME);
    }
    
    public String getProfileUrl() {
        return (String) attributes.get(YahooAttributesDefinition.PROFILE_URL);
    }
    
    public String getTimeZone() {
        return (String) attributes.get(YahooAttributesDefinition.TIME_ZONE);
    }
    
    public Date getUpdated() {
        return (Date) attributes.get(YahooAttributesDefinition.UPDATED);
    }
    
    public String getUri() {
        return (String) attributes.get(YahooAttributesDefinition.URI);
    }
}
