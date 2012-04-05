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
import org.scribe.up.profile.yahoo.YahooProfile;
import org.scribe.up.provider.BaseOAuth10Provider;
import org.scribe.up.util.StringHelper;

/**
 * This class is the OAuth provider to authenticate user in Yahoo. Scope is not used.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.yahoo.YahooProfile} : aboutMe (String), addresses
 * (JsonList&lt;YahooAddress&gt;), birthYear (Integer), birthdate (FormattedDate), created (FormattedDate), displayAge (Integer),
 * disclosures (JsonList&lt;YahooDisclosure&gt;), emails (JsonList&lt;YahooEmail&gt;), familyName (String), gender (Gender), givenName
 * (String), image (YahooImage), interests (JsonList&lt;YahooInterest&gt;), isConnected (Boolean), lang (Locale), location (String),
 * memberSince (FormattedDate), nickname (String), profileUrl (String), timeZone (String), updated (FormattedDate) and uri (String)
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class YahooProvider extends BaseOAuth10Provider {
    
    public final static String TYPE = YahooProvider.class.getSimpleName();
    
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
            json = json.get("profile");
            if (json != null) {
                profile.setId((String) JsonHelper.get(json, "guid"));
                for (String attribute : YahooProfile.getAttributesDefinition().getAttributes()) {
                    profile.addAttribute(attribute, JsonHelper.get(json, attribute));
                }
            }
        }
        return profile;
    }
}
