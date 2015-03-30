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

import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.wordpress.WordPressAttributesDefinition;
import org.pac4j.oauth.profile.wordpress.WordPressProfile;
import org.scribe.builder.api.WordPressApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>This class is the OAuth client to authenticate users in WordPress.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.wordpress.WordPressProfile}.</p>
 * <p>More information at http://developer.wordpress.com/docs/oauth2/</p>
 * 
 * @see org.pac4j.oauth.profile.wordpress.WordPressProfile
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
    protected WordPressClient newClient() {
        return new WordPressClient();
    }
    
    @Override
    protected void internalInit() {
        super.internalInit();
        this.service = new ProxyOAuth20ServiceImpl(new WordPressApi(),
                                                   new OAuthConfig(this.key, this.secret, this.callbackUrl,
                                                                   SignatureType.Header, null, null),
                                                   this.connectTimeout, this.readTimeout, this.proxyHost,
                                                   this.proxyPort, false, true);
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
            profile.setId(JsonHelper.get(json, "ID"));
            for (final String attribute : OAuthAttributesDefinitions.wordPressDefinition.getPrincipalAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
            json = json.get("meta");
            if (json != null) {
                final String attribute = WordPressAttributesDefinition.LINKS;
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
        }
        return profile;
    }
    
    @Override
    protected boolean requiresStateParameter() {
        return false;
    }
    
    @Override
    protected boolean hasBeenCancelled(final WebContext context) {
        return false;
    }
}
