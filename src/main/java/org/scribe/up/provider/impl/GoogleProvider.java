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
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.BaseOAuth10Provider;
import org.scribe.up.session.UserSession;

/**
 * This class is the OAuth provider to authenticate user in Google.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class GoogleProvider extends BaseOAuth10Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(GoogleApi.class).apiKey(key).apiSecret(secret)
            .scope("http://www-opensocial.googleusercontent.com/api/people/").callback(callbackUrl).build();
        mainAttributes.put("profileUrl", null);
        mainAttributes.put("isViewer", null);
        mainAttributes.put("displayName", null);
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
        UserProfile userProfile = new UserProfile();
        JsonNode json = profileHelper.getFirstJsonNode(body);
        if (json != null) {
            json = json.get("entry");
            if (json != null) {
                profileHelper.addIdentifier(userProfile, json, "id");
                for (String attribute : mainAttributes.keySet()) {
                    profileHelper.addAttribute(userProfile, json, attribute, mainAttributes.get(attribute));
                }
                json = json.get("name");
                if (json != null) {
                    profileHelper.addAttribute(userProfile, json, "formatted", null);
                    profileHelper.addAttribute(userProfile, json, "familyName", null);
                    profileHelper.addAttribute(userProfile, json, "givenName", null);
                }
            }
        }
        return userProfile;
    }
}
