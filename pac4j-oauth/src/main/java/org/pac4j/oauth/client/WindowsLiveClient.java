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
import org.pac4j.oauth.profile.windowslive.WindowsLiveProfile;

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.scribe.builder.api.WindowsLiveApi20;

/**
 * <p>This class is the OAuth client to authenticate users in Windows Live (SkyDrive, Hotmail and Messenger).</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.windowslive.WindowsLiveProfile}.</p>
 * <p>More information at http://msdn.microsoft.com/en-us/library/live/hh243641.aspx</p>
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WindowsLiveClient extends BaseOAuth20Client<WindowsLiveProfile> {
    
    public WindowsLiveClient() {
    }
    
    public WindowsLiveClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected Api getApi() {
        return new WindowsLiveApi20();
    }

    @Override
    protected String getOAuthScope() {
        return "wl.basic";
    }
    
    @Override
    protected String getProfileUrl(final Token accessToken) {
        return "https://apis.live.net/v5.0/me";
    }
    
    @Override
    protected WindowsLiveProfile extractUserProfile(final String body) {
        final WindowsLiveProfile profile = new WindowsLiveProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.getElement(json, "id"));
            for (final String attribute : profile.getAttributesDefinition().getPrimaryAttributes()) {
                profile.addAttribute(attribute, JsonHelper.getElement(json, attribute));
            }
        }
        return profile;
    }
}
