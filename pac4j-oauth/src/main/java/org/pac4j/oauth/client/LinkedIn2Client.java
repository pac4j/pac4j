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
import org.pac4j.oauth.client.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.XmlHelper;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2AttributesDefinition;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile;
import org.scribe.builder.api.LinkedInApi20;
import org.scribe.builder.api.StateApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.LinkedInOAuth20ServiceImpl;

/**
 * <p>This class is the OAuth client to authenticate users in LinkedIn (using OAuth 2.0 protocol).</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile}.</p>
 * <p>The scope (by default : <code>r_fullprofile</code>) can be specified using the {@link #setScope(String)} method, as well as the returned
 * fields through the {@link #setFields(String)} method.</p>
 * <p>More information at https://developer.linkedin.com/documents/profile-api</p>
 * 
 * @see org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2Client extends BaseOAuth20Client<LinkedIn2Profile> {
    
    public final static String DEFAULT_SCOPE = "r_fullprofile";
    
    protected String scope = DEFAULT_SCOPE;
    
    protected String fields = "id,first-name,last-name,maiden-name,formatted-name,location,email-address,headline,industry,num-connections,summary,specialties,positions,picture-url,site-standard-profile-request,public-profile-url";
    
    public LinkedIn2Client() {
    }
    
    public LinkedIn2Client(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    @Override
    protected LinkedIn2Client newClient() {
        final LinkedIn2Client newClient = new LinkedIn2Client();
        newClient.setScope(this.scope);
        newClient.setFields(this.fields);
        return newClient;
    }
    
    @Override
    protected void internalInit() {
        super.internalInit();
        CommonHelper.assertNotBlank("scope", this.scope);
        CommonHelper.assertNotBlank("fields", this.fields);
        StateApi20 api20 = new LinkedInApi20();
        this.service = new LinkedInOAuth20ServiceImpl(api20, new OAuthConfig(this.key, this.secret, this.callbackUrl,
                                                                             SignatureType.Header, this.scope, null),
                                                      this.connectTimeout, this.readTimeout, this.proxyHost,
                                                      this.proxyPort);
    }
    
    @Override
    protected boolean requiresStateParameter() {
        return true;
    }
    
    @Override
    protected boolean hasBeenCancelled(final WebContext context) {
        final String error = context.getRequestParameter(OAuthCredentialsException.ERROR);
        final String errorDescription = context.getRequestParameter(OAuthCredentialsException.ERROR_DESCRIPTION);
        // user has denied permissions
        if ("access_denied".equals(error)
            && ("the+user+denied+your+request".equals(errorDescription) || "the user denied your request"
                .equals(errorDescription))) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected String getProfileUrl(final Token accessToken) {
        return "https://api.linkedin.com/v1/people/~:(" + this.fields + ")";
    }
    
    @Override
    protected LinkedIn2Profile extractUserProfile(final String body) {
        LinkedIn2Profile profile = new LinkedIn2Profile();
        profile.setId(XmlHelper.get(body, "id"));
        for (final String attribute : OAuthAttributesDefinitions.linkedin2Definition.getPrincipalAttributes()) {
            profile.addAttribute(attribute, XmlHelper.get(body, attribute));
        }
        String url = XmlHelper.get(XmlHelper.get(body, LinkedIn2AttributesDefinition.SITE_STANDARD_PROFILE_REQUEST),
                                   "url");
        profile.addAttribute(LinkedIn2AttributesDefinition.SITE_STANDARD_PROFILE_REQUEST, url);
        return profile;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public void setScope(final String scope) {
        this.scope = scope;
    }
    
    public String getFields() {
        return this.fields;
    }
    
    public void setFields(final String fields) {
        this.fields = fields;
    }
}
