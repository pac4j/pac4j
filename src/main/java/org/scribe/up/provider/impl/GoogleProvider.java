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
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.Token;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.UserProfileHelper;
import org.scribe.up.profile.google.GoogleObject;
import org.scribe.up.profile.google.GoogleProfile;
import org.scribe.up.provider.BaseOAuth10Provider;
import org.scribe.up.session.UserSession;

/**
 * This class is the OAuth provider to authenticate user in Google. Scope is not used. Attributes are defined in
 * http://code.google.com/intl/fr-FR/apis/contacts/docs/poco/1.0/developers_guide.html.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.google.GoogleProfile} : profileUrl (String), isViewer (Boolean),
 * thumbnailUrl (String), formatted (String), familyName (String), givenName (String), displayName (String), urls (List<GoogleObject>),
 * photos (List<GoogleObject>).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class GoogleProvider extends BaseOAuth10Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(GoogleApi.class).apiKey(key).apiSecret(secret)
            .scope("http://www-opensocial.googleusercontent.com/api/people/").callback(callbackUrl).build();
        mainAttributes.put(GoogleProfile.PROFILE_URL, null);
        mainAttributes.put(GoogleProfile.THUMBNAIL_URL, null);
        mainAttributes.put(GoogleProfile.IS_VIEWER, null);
        mainAttributes.put(GoogleProfile.DISPLAY_NAME, null);
    }
    
    @Override
    public String getAuthorizationUrl(UserSession session) {
        Token requestToken = service.getRequestToken();
        logger.debug("requestToken : {}", requestToken);
        // save requestToken in session
        session.setAttribute(getType() + "#" + REQUEST_TOKEN, requestToken);
        String authorizationUrl = "https://www.google.com/accounts/OAuthAuthorizeToken?oauth_token="
                                  + requestToken.getToken();
        logger.debug("authorizationUrl : {}", authorizationUrl);
        return authorizationUrl;
    }
    
    @Override
    protected String getProfileUrl() {
        return "http://www-opensocial.googleusercontent.com/api/people/@me/@self";
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        GoogleProfile profile = new GoogleProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            json = json.get(GoogleProfile.ENTRY);
            if (json != null) {
                UserProfileHelper.addIdentifier(profile, json, GoogleProfile.ID);
                for (String attribute : mainAttributes.keySet()) {
                    UserProfileHelper.addAttribute(profile, json, attribute, mainAttributes.get(attribute));
                }
                JsonNode subJson = json.get(GoogleProfile.NAME);
                if (subJson != null) {
                    UserProfileHelper.addAttribute(profile, subJson, GoogleProfile.FORMATTED, null);
                    UserProfileHelper.addAttribute(profile, subJson, GoogleProfile.FAMILY_NAME, null);
                    UserProfileHelper.addAttribute(profile, subJson, GoogleProfile.GIVEN_NAME, null);
                }
                // urls
                subJson = json.get(GoogleProfile.URLS);
                if (subJson != null) {
                    UserProfileHelper.addAttribute(profile, GoogleProfile.URLS,
                                                   UserProfileHelper.getListObject(subJson, GoogleObject.class));
                }
                // photos
                subJson = json.get(GoogleProfile.PHOTOS);
                if (subJson != null) {
                    UserProfileHelper.addAttribute(profile, GoogleProfile.PHOTOS,
                                                   UserProfileHelper.getListObject(subJson, GoogleObject.class));
                }
            }
        }
        return profile;
    }
}
