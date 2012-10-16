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

import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.up.addon_to_scribe.ExtendedOAuth20ServiceImpl;
import org.scribe.up.addon_to_scribe.GoogleApi20;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.google.Google2Profile;
import org.scribe.up.provider.BaseOAuth20Provider;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is the OAuth provider to authenticate user in Google using OAuth protocol version 2.0. Scope is used to retrieve email,
 * profile or both (default).<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.google.Google2Profile} : email (String), verified_email (Boolean), name
 * (String), given_name (String), family_name (String), link (String), picture (String), gender (Gender) and locale (Locale).<br />
 * More information at https://developers.google.com/accounts/docs/OAuth2Login
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2Provider extends BaseOAuth20Provider {
    
    public enum Google2Scope {
        EMAIL,
        PROFILE,
        EMAIL_AND_PROFILE
    };
    
    protected final String PROFILE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    
    protected final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    
    protected Google2Scope scope = Google2Scope.EMAIL_AND_PROFILE;
    
    protected String scopeValue;
    
    @Override
    protected Google2Provider newProvider() {
        final Google2Provider newProvider = new Google2Provider();
        newProvider.setScope(this.scope);
        return newProvider;
    }
    
    @Override
    protected void internalInit() {
        this.service = new ExtendedOAuth20ServiceImpl(new GoogleApi20(), new OAuthConfig(this.key, this.secret,
                                                                                         this.callbackUrl,
                                                                                         SignatureType.Header,
                                                                                         this.scopeValue, null),
                                                      this.proxyHost, this.proxyPort);
    }
    
    @Override
    protected String getProfileUrl() {
        return "https://www.googleapis.com/oauth2/v2/userinfo";
    }
    
    @Override
    protected UserProfile extractUserProfile(final String body) {
        final Google2Profile profile = new Google2Profile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, "id"));
            for (final String attribute : AttributesDefinitions.google2Definition.getPrincipalAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
        }
        return profile;
    }
    
    public Google2Scope getScope() {
        return this.scope;
    }
    
    public void setScope(final Google2Scope scope) {
        this.scope = scope;
        if (scope == Google2Scope.EMAIL) {
            this.scopeValue = this.EMAIL_SCOPE;
        } else if (scope == Google2Scope.PROFILE) {
            this.scopeValue = this.PROFILE_SCOPE;
        } else {
            this.scopeValue = this.PROFILE_SCOPE + " " + this.EMAIL_SCOPE;
        }
    }
}
