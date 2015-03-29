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

import java.util.Iterator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;
import org.scribe.builder.api.CasOAuthWrapperApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>This class is the OAuth client to authenticate users on CAS servers using OAuth wrapper.</p>
 * <p>The url of the OAuth endpoint of the CAS server must be set by using the {@link #setCasOAuthUrl(String)} method.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile}.</p>
 * <p>More information at https://wiki.jasig.org/display/CASUM/OAuth+server+support</p>
 * 
 * @see org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class CasOAuthWrapperClient extends BaseOAuth20Client<CasOAuthWrapperProfile> {
    
    private String casOAuthUrl;
    
    private boolean springSecurityCompliant = false;
    
    public CasOAuthWrapperClient() {
    }
    
    public CasOAuthWrapperClient(final String key, final String secret, final String casOAuthUrl) {
        setKey(key);
        setSecret(secret);
        this.casOAuthUrl = casOAuthUrl;
    }
    
    @Override
    protected CasOAuthWrapperClient newClient() {
        final CasOAuthWrapperClient newClient = new CasOAuthWrapperClient();
        newClient.setCasOAuthUrl(this.casOAuthUrl);
        newClient.setSpringSecurityCompliant(this.springSecurityCompliant);
        return newClient;
    }
    
    @Override
    protected void internalInit() {
        super.internalInit();
        CommonHelper.assertNotBlank("casOAuthUrl", this.casOAuthUrl);
        this.service = new ProxyOAuth20ServiceImpl(new CasOAuthWrapperApi20(this.casOAuthUrl,
                                                                            this.springSecurityCompliant),
                                                   new OAuthConfig(this.key, this.secret, this.callbackUrl,
                                                                   SignatureType.Header, null, null),
                                                   this.connectTimeout, this.readTimeout, this.proxyHost,
                                                   this.proxyPort, false, true);
    }
    
    @Override
    protected String getProfileUrl(final Token accessToken) {
        return this.casOAuthUrl + "/profile";
    }
    
    @Override
    protected CasOAuthWrapperProfile extractUserProfile(final String body) {
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
    
    public String getCasOAuthUrl() {
        return this.casOAuthUrl;
    }
    
    public void setCasOAuthUrl(final String casOAuthUrl) {
        this.casOAuthUrl = casOAuthUrl;
    }
    
    public boolean isSpringSecurityCompliant() {
        return this.springSecurityCompliant;
    }
    
    public void setSpringSecurityCompliant(final boolean springSecurityCompliant) {
        this.springSecurityCompliant = springSecurityCompliant;
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
