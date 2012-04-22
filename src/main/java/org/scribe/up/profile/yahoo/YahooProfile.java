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
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.ProfileDefinitions;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.impl.YahooProvider;

/**
 * This class is the user profile for Yahoo with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class YahooProfile extends UserProfile {
    
    private static final long serialVersionUID = -6832062183539648599L;
    
    protected AttributesDefinition getAttributesDefinition() {
        return ProfileDefinitions.yahooDefinition;
    }
    
    protected String getProviderType() {
        return YahooProvider.TYPE;
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
    
    public static boolean isTypedIdOf(String id) {
        if (id != null && id.startsWith(YahooProvider.TYPE + SEPARATOR)) {
            return true;
        } else {
            return false;
        }
    }
    
    public String getAboutMe() {
        return (String) attributes.get(YahooProfileDefinition.ABOUT_ME);
    }
    
    public List<YahooAddress> getAddresses() {
        return (List<YahooAddress>) attributes.get(YahooProfileDefinition.ADDRESSES);
    }
    
    public int getBirthYear() {
        return getSafeInteger((Integer) attributes.get(YahooProfileDefinition.BIRTH_YEAR));
    }
    
    public boolean isBirthYearDefined() {
        return attributes.get(YahooProfileDefinition.BIRTH_YEAR) != null;
    }
    
    public Date getBirthdate() {
        return (Date) attributes.get(YahooProfileDefinition.BIRTHDATE);
    }
    
    public Date getCreated() {
        return (Date) attributes.get(YahooProfileDefinition.CREATED);
    }
    
    public int getDisplayAge() {
        return getSafeInteger((Integer) attributes.get(YahooProfileDefinition.DISPLAY_AGE));
    }
    
    public boolean isDisplayAgeDefined() {
        return attributes.get(YahooProfileDefinition.DISPLAY_AGE) != null;
    }
    
    public List<YahooDisclosure> getDisclosures() {
        return (List<YahooDisclosure>) attributes.get(YahooProfileDefinition.DISCLOSURES);
    }
    
    public List<YahooEmail> getEmails() {
        return (List<YahooEmail>) attributes.get(YahooProfileDefinition.EMAILS);
    }
    
    public String getFamilyName() {
        return (String) attributes.get(YahooProfileDefinition.FAMILY_NAME);
    }
    
    public Gender getGender() {
        return (Gender) attributes.get(YahooProfileDefinition.GENDER);
    }
    
    public String getGivenName() {
        return (String) attributes.get(YahooProfileDefinition.GIVEN_NAME);
    }
    
    public YahooImage getImage() {
        return (YahooImage) attributes.get(YahooProfileDefinition.IMAGE);
    }
    
    public List<YahooInterest> getInterests() {
        return (List<YahooInterest>) attributes.get(YahooProfileDefinition.INTERESTS);
    }
    
    public boolean isConnected() {
        return getSafeBoolean((Boolean) attributes.get(YahooProfileDefinition.IS_CONNECTED));
    }
    
    public boolean isConnectedDefined() {
        return attributes.get(YahooProfileDefinition.IS_CONNECTED) != null;
    }
    
    public Locale getLang() {
        return (Locale) attributes.get(YahooProfileDefinition.LANG);
    }
    
    public String getLocation() {
        return (String) attributes.get(YahooProfileDefinition.LOCATION);
    }
    
    public Date getMemberSince() {
        return (Date) attributes.get(YahooProfileDefinition.MEMBER_SINCE);
    }
    
    public String getNickname() {
        return (String) attributes.get(YahooProfileDefinition.NICKNAME);
    }
    
    public String getProfileUrl() {
        return (String) attributes.get(YahooProfileDefinition.PROFILE_URL);
    }
    
    public String getTimeZone() {
        return (String) attributes.get(YahooProfileDefinition.TIME_ZONE);
    }
    
    public Date getUpdated() {
        return (Date) attributes.get(YahooProfileDefinition.UPDATED);
    }
    
    public String getUri() {
        return (String) attributes.get(YahooProfileDefinition.URI);
    }
}
