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

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.up.addon_to_scribe.CasOAuthWrapperApi20;
import org.scribe.up.addon_to_scribe.ProxyOAuth20ServiceImpl;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.casoauthwrapper.CasOAuthWrapperProfile;
import org.scribe.up.provider.BaseOAuth20Provider;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is the OAuth provider to authenticate user in sites using OAuth wrapper for CAS server.
 * <p />
 * The url of the OAuth endpoint of the CAS server must be set by using the {@link #setCasServerUrl(String)} method.
 * <p />
 * It returns a {@link org.scribe.up.profile.casoauthwrapper.CasOAuthWrapperProfile}.
 * <p />
 * More information at https://wiki.jasig.org/display/CASUM/OAuth+server+support
 * 
 * @see org.scribe.up.profile.casoauthwrapper.CasOAuthWrapperProfile
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class CasOAuthWrapperProvider extends BaseOAuth20Provider {
    
    private String casServerUrl;
    
    @Override
    protected CasOAuthWrapperProvider newProvider() {
        final CasOAuthWrapperProvider newProvider = new CasOAuthWrapperProvider();
        newProvider.setCasServerUrl(this.casServerUrl);
        return newProvider;
    }
    
    @Override
    protected void internalInit() {
        if (StringUtils.isBlank(this.casServerUrl)) {
            throw new IllegalArgumentException("casServerUrl cannot be blank");
        }
        this.service = new ProxyOAuth20ServiceImpl(new CasOAuthWrapperApi20(this.casServerUrl),
                                                   new OAuthConfig(this.key, this.secret, this.callbackUrl,
                                                                   SignatureType.Header, null, null), this.proxyHost,
                                                   this.proxyPort);
    }
    
    @Override
    protected String getProfileUrl() {
        return this.casServerUrl + "/profile";
    }
    
    @Override
    protected UserProfile extractUserProfile(final String body) {
        final CasOAuthWrapperProfile userProfile = new CasOAuthWrapperProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            userProfile.setId(JsonHelper.get(json, "id"));
            json = json.get("attributes");
            if (json != null) {
                final Iterator<JsonNode> nodes = json.iterator();
                while (nodes.hasNext()) {
                    json = nodes.next();
                    final String attribute = json.fieldNames().next();
                    userProfile.addAttribute(attribute, JsonHelper.get(json, attribute));
                }
            }
        }
        return userProfile;
    }
    
    public String getCasServerUrl() {
        return this.casServerUrl;
    }
    
    public void setCasServerUrl(final String casServerUrl) {
        this.casServerUrl = casServerUrl;
    }
}
