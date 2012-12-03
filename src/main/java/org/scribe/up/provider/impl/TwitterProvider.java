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

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.up.addon_to_scribe.ProxyOAuth10aServiceImpl;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.OAuthAttributesDefinitions;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.twitter.TwitterProfile;
import org.scribe.up.provider.BaseOAuth10Provider;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is the OAuth provider to authenticate user in Twitter.
 * <p />
 * You can define if a screen should always been displayed for authorization confirmation by using the
 * {@link #setAlwaysConfirmAuthorization(boolean)} method (<code>false</code> by default).
 * <p />
 * It returns a {@link org.scribe.up.profile.twitter.TwitterProfile}.
 * <p />
 * More information at https://dev.twitter.com/docs/api/1/get/account/verify_credentials
 * 
 * @see org.scribe.up.profile.twitter.TwitterProfile
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TwitterProvider extends BaseOAuth10Provider {
    
    private boolean alwaysConfirmAuthorization = false;
    
    @Override
    protected TwitterProvider newProvider() {
        return new TwitterProvider();
    }
    
    @Override
    protected void internalInit() {
        DefaultApi10a api;
        if (this.alwaysConfirmAuthorization == false) {
            api = new TwitterApi.Authenticate();
        } else {
            api = new TwitterApi();
        }
        this.service = new ProxyOAuth10aServiceImpl(api, new OAuthConfig(this.key, this.secret, this.callbackUrl,
                                                                         SignatureType.Header, null, null),
                                                    this.proxyHost, this.proxyPort);
    }
    
    @Override
    protected String getProfileUrl() {
        return "http://api.twitter.com/1/account/verify_credentials.json";
    }
    
    @Override
    protected UserProfile extractUserProfile(final String body) {
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
