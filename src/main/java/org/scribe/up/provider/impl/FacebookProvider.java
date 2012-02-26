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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.up.profile.DateConverter;
import org.scribe.up.profile.GenderConverter;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.LocaleConverter;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.UserProfileHelper;
import org.scribe.up.profile.facebook.FacebookEducation;
import org.scribe.up.profile.facebook.FacebookObject;
import org.scribe.up.profile.facebook.FacebookProfileHelper;
import org.scribe.up.profile.facebook.FacebookRelationshipStatusConverter;
import org.scribe.up.profile.facebook.FacebookWork;
import org.scribe.up.provider.BaseOAuth20Provider;

/**
 * This class is the OAuth provider to authenticate user in Facebook. Specific scopes and attributes are defined in
 * http://developers.facebook.com/docs/reference/api/user/.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.UserProfile} : name (String), first_name (String), middle_name (String),
 * last_name (String), gender (Gender), locale (Locale), languages (List<FacebookObject>), link (String), username (String), third_party_id
 * (String), timezone (Integer), updated_time (Date), verified (Boolean), bio (String), birthday (Date), education
 * (List<FacebookEducation>), email (String), hometown (FacebookObject), interested_in (List<String>), location (FacebookObject), political
 * (String), favorite_athletes (List<FacebookObject>), favorite_teams (List<FacebookObject>), quotes (String), relationship_status
 * (FacebookRelationshipStatus), religion (String), significant_other (FacebookObject), website (String), work (List<FacebookWork>).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class FacebookProvider extends BaseOAuth20Provider {
    
    @Override
    protected void internalInit() {
        if (scope != null) {
            service = new ServiceBuilder().provider(FacebookApi.class).apiKey(key).apiSecret(secret)
                .callback(callbackUrl).scope(scope).build();
        } else {
            service = new ServiceBuilder().provider(FacebookApi.class).apiKey(key).apiSecret(secret)
                .callback(callbackUrl).build();
        }
        String[] names = new String[] {
            "name", "first_name", "middle_name", "last_name", "link", "username", "third_party_id", "timezone",
            "verified", "bio", "email", "political", "quotes", "religion", "website"
        };
        for (String name : names) {
            mainAttributes.put(name, null);
        }
        mainAttributes.put("gender", new GenderConverter("male"));
        mainAttributes.put("locale", new LocaleConverter());
        mainAttributes.put("updated_time", new DateConverter("yyyy-MM-dd'T'HH:mm:ssz"));
        mainAttributes.put("birthday", new DateConverter("MM/dd/yyyy"));
        mainAttributes.put("relationship_status", new FacebookRelationshipStatusConverter());
    }
    
    @Override
    protected String getProfileUrl() {
        return "https://graph.facebook.com/me";
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        UserProfile userProfile = new UserProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            UserProfileHelper.addIdentifier(userProfile, json, "id");
            for (String attribute : mainAttributes.keySet()) {
                UserProfileHelper.addAttribute(userProfile, json, attribute, mainAttributes.get(attribute));
            }
        }
        // languages
        JsonNode subJson = json.get("languages");
        if (subJson != null) {
            UserProfileHelper.addAttribute(userProfile, "languages",
                                           FacebookProfileHelper.getListFacebookObject(subJson));
        }
        // installed
        // education
        subJson = json.get("education");
        if (subJson != null) {
            List<FacebookEducation> education = new ArrayList<FacebookEducation>();
            Iterator<JsonNode> educationIterator = subJson.getElements();
            while (educationIterator.hasNext()) {
                JsonNode jsonEducation = educationIterator.next();
                FacebookEducation oneEducation = new FacebookEducation(jsonEducation);
                education.add(oneEducation);
            }
            UserProfileHelper.addAttribute(userProfile, "education", education);
        }
        // hometown
        subJson = json.get("hometown");
        if (subJson != null) {
            UserProfileHelper.addAttribute(userProfile, "hometown", new FacebookObject(subJson));
        }
        // interested_in
        subJson = json.get("interested_in");
        if (subJson != null) {
            Iterator<JsonNode> interestIterator = subJson.getElements();
            List<String> interested_in = new ArrayList<String>();
            while (interestIterator.hasNext()) {
                JsonNode interest = interestIterator.next();
                interested_in.add(interest.getTextValue());
            }
            UserProfileHelper.addAttribute(userProfile, "interested_in", interested_in);
        }
        // location
        subJson = json.get("location");
        if (subJson != null) {
            UserProfileHelper.addAttribute(userProfile, "location", new FacebookObject(subJson));
        }
        // favorite_athletes
        subJson = json.get("favorite_athletes");
        if (subJson != null) {
            UserProfileHelper.addAttribute(userProfile, "favorite_athletes",
                                           FacebookProfileHelper.getListFacebookObject(subJson));
        }
        // favorite_teams
        subJson = json.get("favorite_teams");
        if (subJson != null) {
            UserProfileHelper.addAttribute(userProfile, "favorite_teams",
                                           FacebookProfileHelper.getListFacebookObject(subJson));
        }
        // significant_other
        subJson = json.get("significant_other");
        if (subJson != null) {
            UserProfileHelper.addAttribute(userProfile, "significant_other", new FacebookObject(subJson));
        }
        // video_upload_limits
        // work
        subJson = json.get("work");
        if (subJson != null) {
            List<FacebookWork> work = new ArrayList<FacebookWork>();
            Iterator<JsonNode> workIterator = subJson.getElements();
            while (workIterator.hasNext()) {
                JsonNode jsonWork = workIterator.next();
                FacebookWork oneWork = new FacebookWork(jsonWork);
                work.add(oneWork);
            }
            UserProfileHelper.addAttribute(userProfile, "work", work);
        }
        return userProfile;
    }
}
