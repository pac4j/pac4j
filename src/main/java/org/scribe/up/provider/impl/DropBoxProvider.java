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

import java.util.Map;

import org.scribe.builder.api.DropBoxApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.up.addon_to_scribe.ProxyOAuth10aServiceImpl;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.dropbox.DropBoxProfile;
import org.scribe.up.provider.BaseOAuth10Provider;
import org.scribe.up.session.UserSession;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is the OAuth provider to authenticate user in DropBox. Scope is not used.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.dropbox.DropBoxProfile} : referral_link (String), display_name (String),
 * country (Locale), shared (Integer), quota (Integer) and normal (Integer).<br />
 * More information at https://www.dropbox.com/developers/reference/api#account-info
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxProvider extends BaseOAuth10Provider {
    
    @Override
    protected DropBoxProvider newProvider() {
        return new DropBoxProvider();
    }
    
    @Override
    protected void internalInit() {
        this.service = new ProxyOAuth10aServiceImpl(new DropBoxApi(),
                                                    new OAuthConfig(this.key, this.secret, this.callbackUrl,
                                                                    SignatureType.Header, null, null), this.proxyHost,
                                                    this.proxyPort);
    }
    
    @Override
    protected String getProfileUrl() {
        return "https://api.dropbox.com/1/account/info";
    }
    
    @Override
    protected OAuthCredential extractCredentialFromParameters(final UserSession session,
                                                              final Map<String, String[]> parameters) {
        // get tokenRequest from user session
        final Token tokenRequest = (Token) session.getAttribute(getRequestTokenSessionAttributeName());
        logger.debug("tokenRequest : {}", tokenRequest);
        // don't get parameters from url
        // token and verifier are equals and extracted from saved request token
        final String token = tokenRequest.getToken();
        logger.debug("token = verifier : {}", token);
        return new OAuthCredential(tokenRequest, token, token, getType());
    }
    
    @Override
    protected UserProfile extractUserProfile(final String body) {
        final DropBoxProfile profile = new DropBoxProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, "uid"));
            for (final String attribute : AttributesDefinitions.dropBoxDefinition.getPrincipalAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
            json = (JsonNode) JsonHelper.get(json, "quota_info");
            if (json != null) {
                for (final String attribute : AttributesDefinitions.dropBoxDefinition.getOtherAttributes()) {
                    profile.addAttribute(attribute, JsonHelper.get(json, attribute));
                }
            }
        }
        return profile;
    }
}
