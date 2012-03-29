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
package org.scribe.up.provider.impl;

import org.codehaus.jackson.JsonNode;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.Token;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.UserProfileHelper;
import org.scribe.up.profile.converter.DateConverter;
import org.scribe.up.profile.converter.GenderConverter;
import org.scribe.up.profile.yahoo.YahooAddress;
import org.scribe.up.profile.yahoo.YahooDisclosure;
import org.scribe.up.profile.yahoo.YahooEmail;
import org.scribe.up.profile.yahoo.YahooImage;
import org.scribe.up.profile.yahoo.YahooInterest;
import org.scribe.up.profile.yahoo.YahooProfile;
import org.scribe.up.provider.BaseOAuth10Provider;
import org.scribe.up.util.StringHelper;

/**
 * This class is the OAuth provider to authenticate user in Yahoo. Scope is not used.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.yahoo.YahooProfile} : aboutMe (String), addresses (List&lt;
 * {@link org.scribe.up.profile.yahoo.YahooAddress}&gt;), birthYear (Integer), birthdate (Date), created (Date), displayAge (Integer),
 * disclosures (List&lt;{@link org.scribe.up.profile.yahoo.YahooDisclosure}&gt;), emails (List&lt;
 * {@link org.scribe.up.profile.yahoo.YahooEmail}&gt;), familyName (String), gender (Gender), givenName (String), image (
 * {@link org.scribe.up.profile.yahoo.YahooImage}), interests (List&lt;{@link org.scribe.up.profile.yahoo.YahooInterest}&gt;), isConnected
 * (Boolean), lang (Locale), location (String), memberSince (Date), nickname (String), profileUrl (String), timeZone (String), updated
 * (Date) and uri (String)
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class YahooProvider extends BaseOAuth10Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(YahooApi.class).apiKey(key).apiSecret(secret).callback(callbackUrl)
            .build();
        // sounds partially obsolete : http://developer.yahoo.com/social/rest_api_guide/extended-profile-resource.html
        String[] names = new String[] {
            YahooProfile.BIRTH_YEAR, YahooProfile.ABOUT_ME, YahooProfile.DISPLAY_AGE, YahooProfile.FAMILY_NAME,
            YahooProfile.GIVEN_NAME, YahooProfile.IS_CONNECTED, YahooProfile.LOCATION, YahooProfile.NICKNAME,
            YahooProfile.PROFILE_URL, YahooProfile.TIME_ZONE, YahooProfile.URI
        };
        for (String name : names) {
            mainAttributes.put(name, null);
        }
        mainAttributes.put(YahooProfile.BIRTHDATE, new DateConverter("MM/dd"));
        mainAttributes.put(YahooProfile.GENDER, new GenderConverter("m", "f"));
        DateConverter dateConverter = new DateConverter("yyyy-MM-dd'T'HH:mm:ss'Z'");
        mainAttributes.put(YahooProfile.CREATED, dateConverter);
        mainAttributes.put(YahooProfile.MEMBER_SINCE, dateConverter);
        mainAttributes.put(YahooProfile.UPDATED, dateConverter);
        mainAttributes.put(YahooProfile.LANG, localeConverter);
    }
    
    @Override
    protected String getProfileUrl() {
        return "http://social.yahooapis.com/v1/me/guid?format=xml";
    }
    
    @Override
    public UserProfile getUserProfile(Token accessToken) {
        // get the guid : http://developer.yahoo.com/social/rest_api_guide/introspective-guid-resource.html
        String body = sendRequestForProfile(accessToken, getProfileUrl());
        if (body == null) {
            return null;
        }
        String guid = StringHelper.substringBetween(body, "<value>", "</value>");
        logger.debug("guid : {}", guid);
        // then the profile with the guid
        if (StringHelper.isNotBlank(guid)) {
            body = sendRequestForProfile(accessToken, "http://social.yahooapis.com/v1/user/" + guid
                                                      + "/profile?format=json");
            if (body == null) {
                return null;
            }
        }
        return extractUserProfile(body);
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        YahooProfile profile = new YahooProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            json = json.get(YahooProfile.PROFILE);
            if (json != null) {
                UserProfileHelper.addIdentifier(profile, json, YahooProfile.GUID);
                for (String attribute : mainAttributes.keySet()) {
                    UserProfileHelper.addAttribute(profile, json, attribute, mainAttributes.get(attribute));
                }
                // addresses
                JsonNode subJson = json.get(YahooProfile.ADDRESSES);
                if (subJson != null) {
                    UserProfileHelper.addAttribute(profile, YahooProfile.ADDRESSES,
                                                   UserProfileHelper.getListObject(subJson, YahooAddress.class));
                }
                // disclosures
                subJson = json.get(YahooProfile.DISCLOSURES);
                if (subJson != null) {
                    UserProfileHelper.addAttribute(profile, YahooProfile.DISCLOSURES,
                                                   UserProfileHelper.getListObject(subJson, YahooDisclosure.class));
                }
                // emails
                subJson = json.get(YahooProfile.EMAILS);
                if (subJson != null) {
                    UserProfileHelper.addAttribute(profile, YahooProfile.EMAILS,
                                                   UserProfileHelper.getListObject(subJson, YahooEmail.class));
                }
                // image
                subJson = json.get(YahooProfile.IMAGE);
                if (subJson != null) {
                    UserProfileHelper.addAttribute(profile, YahooProfile.IMAGE, new YahooImage(subJson));
                }
                // interests
                subJson = json.get(YahooProfile.INTERESTS);
                if (subJson != null) {
                    UserProfileHelper.addAttribute(profile, YahooProfile.INTERESTS,
                                                   UserProfileHelper.getListObject(subJson, YahooInterest.class));
                }
            }
        }
        return profile;
    }
}
