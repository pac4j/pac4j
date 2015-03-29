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
import org.pac4j.oauth.profile.windowslive.WindowsLiveProfile;
import org.scribe.builder.api.WindowsLiveApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>This class is the OAuth client to authenticate users in Windows Live (SkyDrive, Hotmail and Messenger).</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.windowslive.WindowsLiveProfile}.</p>
 * <p>More information at http://msdn.microsoft.com/en-us/library/live/hh243641.aspx</p>
 * 
 * @see org.pac4j.oauth.profile.windowslive.WindowsLiveProfile
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
    protected WindowsLiveClient newClient() {
        return new WindowsLiveClient();
    }
    
    @Override
    protected void internalInit() {
        super.internalInit();
        this.service = new ProxyOAuth20ServiceImpl(new WindowsLiveApi(), new OAuthConfig(this.key, this.secret,
                                                                                  this.callbackUrl,
                                                                                  SignatureType.Header, "wl.basic",
                                                                                  null), this.connectTimeout,
                                                   this.readTimeout, this.proxyHost, this.proxyPort);
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
            profile.setId(JsonHelper.get(json, "id"));
            for (final String attribute : OAuthAttributesDefinitions.windowsLiveDefinition.getAllAttributes()) {
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
