/*
  Copyright 2012 Jérôme Leleu

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

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.Token;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.UserProfileHelper;
import org.scribe.up.provider.BaseOAuth10Provider;

/**
 * This class is the Yahoo provider to authenticate user in Yahoo. It extends the <b>BaseOAuth10Provider</b> class.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public class YahooProvider extends BaseOAuth10Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(YahooApi.class).apiKey(key).apiSecret(secret).callback(callbackUrl)
            .build();
    }
    
    @Override
    protected String getProfileUrl() {
        return "http://social.yahooapis.com/v1/me/guid?format=xml";
    }
    
    @Override
    public UserProfile getUserProfile(Token accessToken) {
        String body = sendRequestForProfile(accessToken, getProfileUrl());
        if (body == null) {
            return null;
        }
        String guid = UserProfileHelper.extractString(body, "<value>", "</value>");
        logger.debug("guid : {}", guid);
        if (guid != null && !"".equals(guid.trim())) {
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
        UserProfile userProfile = new UserProfile();
        try {
            JSONObject jsonBody = new JSONObject(body);
            JSONObject json = jsonBody.getJSONObject("profile");
            UserProfileHelper.addIdentifier(userProfile, json, "guid");
            UserProfileHelper.addAttribute(userProfile, json, "uri");
            UserProfileHelper.addAttribute(userProfile, json, "birthdate");
            UserProfileHelper.addAttribute(userProfile, json, "created");
            UserProfileHelper.addAttribute(userProfile, json, "familyName");
            UserProfileHelper.addAttribute(userProfile, json, "gender");
            UserProfileHelper.addAttribute(userProfile, json, "givenName");
            UserProfileHelper.addAttribute(userProfile, json, "lang");
            UserProfileHelper.addAttribute(userProfile, json, "memberSince");
            UserProfileHelper.addAttribute(userProfile, json, "nickname");
            UserProfileHelper.addAttribute(userProfile, json, "profileUrl");
            UserProfileHelper.addAttribute(userProfile, json, "timeZone");
            UserProfileHelper.addAttribute(userProfile, json, "updated");
            UserProfileHelper.addAttribute(userProfile, json, "isConnected");
        } catch (JSONException e) {
            logger.error("JSON exception", e);
        }
        return userProfile;
    }
}
