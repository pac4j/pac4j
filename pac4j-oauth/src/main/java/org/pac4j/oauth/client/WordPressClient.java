/*
  Copyright 2012 - 2015 pac4j organization

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
package org.pac4j.oauth.client;

import com.github.scribejava.core.builder.api.Api;
import com.github.scribejava.core.model.Token;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.wordpress.WordPressAttributesDefinition;
import org.pac4j.oauth.profile.wordpress.WordPressProfile;
import org.pac4j.scribe.builder.api.WordPressApi20;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>This class is the OAuth client to authenticate users in WordPress.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.wordpress.WordPressProfile}.</p>
 * <p>More information at http://developer.wordpress.com/docs/oauth2/</p>
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressClient extends BaseOAuth20Client<WordPressProfile> {
    
    public WordPressClient() {
        setTokenAsHeader(true);
    }
    
    public WordPressClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
        setTokenAsHeader(true);
    }
    
    @Override
    protected Api getApi() {
        return new WordPressApi20();
    }

    @Override
    protected  boolean hasOAuthGrantType() {
        return true;
    }

    @Override
    protected String getProfileUrl(final Token accessToken) {
        return "https://public-api.wordpress.com/rest/v1/me/?pretty=1";
    }
    
    @Override
    protected WordPressProfile extractUserProfile(final String body) {
        final WordPressProfile profile = new WordPressProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.getElement(json, "ID"));
            for (final String attribute : profile.getAttributesDefinition().getPrimaryAttributes()) {
                profile.addAttribute(attribute, JsonHelper.getElement(json, attribute));
            }
            json = json.get("meta");
            if (json != null) {
                final String attribute = WordPressAttributesDefinition.LINKS;
                profile.addAttribute(attribute, JsonHelper.getElement(json, attribute));
            }
        }
        return profile;
    }
}
