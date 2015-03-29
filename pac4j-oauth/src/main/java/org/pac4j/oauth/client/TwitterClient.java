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
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.twitter.TwitterProfile;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth10aServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>This class is the OAuth client to authenticate users in Twitter.</p>
 * <p>You can define if a screen should always been displayed for authorization confirmation by using the
 * {@link #setAlwaysConfirmAuthorization(boolean)} method (<code>false</code> by default).</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.twitter.TwitterProfile}.</p>
 * <p>More information at https://dev.twitter.com/docs/api/1/get/account/verify_credentials</p>
 * 
 * @see org.pac4j.oauth.profile.twitter.TwitterProfile
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TwitterClient extends BaseOAuth10Client<TwitterProfile> {
    
    private boolean alwaysConfirmAuthorization = false;
    
    public TwitterClient() {
    }
    
    public TwitterClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    @Override
    protected TwitterClient newClient() {
        return new TwitterClient();
    }
    
    @Override
    protected void internalInit() {
        super.internalInit();
        DefaultApi10a api;
        if (this.alwaysConfirmAuthorization == false) {
            api = new TwitterApi.Authenticate();
        } else {
            api = new TwitterApi();
        }
        this.service = new ProxyOAuth10aServiceImpl(api, new OAuthConfig(this.key, this.secret, this.callbackUrl,
                                                                         SignatureType.Header, null, null),
                                                    this.connectTimeout, this.readTimeout, this.proxyHost,
                                                    this.proxyPort);
    }
    
    @Override
    protected String getProfileUrl(final Token accessToken) {
        return "https://api.twitter.com/1.1/account/verify_credentials.json";
    }
    
    @Override
    protected boolean hasBeenCancelled(final WebContext context) {
        final String denied = context.getRequestParameter("denied");
        if (CommonHelper.isNotBlank(denied)) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected TwitterProfile extractUserProfile(final String body) {
        final TwitterProfile profile = new TwitterProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, "id"));
            for (final String attribute : OAuthAttributesDefinitions.twitterDefinition.getAllAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
        }
        return profile;
    }
    
    public boolean isAlwaysConfirmAuthorization() {
        return this.alwaysConfirmAuthorization;
    }
    
    public void setAlwaysConfirmAuthorization(final boolean alwaysConfirmAuthorization) {
        this.alwaysConfirmAuthorization = alwaysConfirmAuthorization;
    }
}
