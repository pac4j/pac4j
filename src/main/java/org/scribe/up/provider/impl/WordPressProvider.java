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

import org.scribe.builder.ServiceBuilder;
import org.scribe.up.addon_to_scribe.WordPressApi;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.wordpress.WordPressAttributesDefinition;
import org.scribe.up.profile.wordpress.WordPressProfile;
import org.scribe.up.provider.BaseOAuth20Provider;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is the OAuth provider to authenticate user in WordPress. Scope is not used.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.wordpress.WordPressProfile} : display_name (String), username (String),
 * email (String), primary_blog (Integer), avatar_URL (String), profile_URL (String) and links (WordPressLinks).<br />
 * More information at http://developer.wordpress.com/docs/oauth2/
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressProvider extends BaseOAuth20Provider {
    
    @Override
    protected WordPressProvider newProvider() {
        return new WordPressProvider();
    }
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(WordPressApi.class).apiKey(key).apiSecret(secret).callback(callbackUrl)
            .build();
    }
    
    @Override
    protected String getProfileUrl() {
        return "https://public-api.wordpress.com/rest/v1/me/?pretty=1";
    }
    
    @Override
    protected UserProfile extractUserProfile(final String body) {
        WordPressProfile profile = new WordPressProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, "ID"));
            for (String attribute : AttributesDefinitions.wordPressDefinition.getPrincipalAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
            json = json.get("meta");
            if (json != null) {
                String attribute = WordPressAttributesDefinition.LINKS;
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
        }
        return profile;
    }
}
