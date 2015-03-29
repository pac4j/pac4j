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

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.linkedin.LinkedInAttributesDefinition;
import org.pac4j.oauth.profile.linkedin.LinkedInProfile;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth10aServiceImpl;

/**
 * <p>Deprecated! Upgrade to {@link LinkedIn2Client}!</p>
 * <p>This class is the OAuth client to authenticate user in LinkedIn.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.linkedin.LinkedInProfile}.</p>
 * <p>More information at https://developer.linkedin.com/documents/profile-api</p>
 * 
 * @see org.pac4j.oauth.profile.linkedin.LinkedInProfile
 * @author Jerome Leleu
 * @since 1.0.0
 */
@Deprecated
public class LinkedInClient extends BaseOAuth10Client<LinkedInProfile> {
    
    public LinkedInClient() {
    }
    
    public LinkedInClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    @Override
    protected LinkedInClient newClient() {
        return new LinkedInClient();
    }
    
    @Override
    protected void internalInit() {
        super.internalInit();
        this.service = new ProxyOAuth10aServiceImpl(new LinkedInApi(),
                                                    new OAuthConfig(this.key, this.secret, this.callbackUrl,
                                                                    SignatureType.Header, null, null),
                                                    this.connectTimeout, this.readTimeout, this.proxyHost,
                                                    this.proxyPort);
    }
    
    @Override
    protected String getProfileUrl(final Token accessToken) {
        return "http://api.linkedin.com/v1/people/~";
    }
    
    @Override
    protected boolean hasBeenCancelled(final WebContext context) {
        final String problem = context.getRequestParameter("oauth_problem");
        if ("user_refused".equals(problem)) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected LinkedInProfile extractUserProfile(final String body) {
        final LinkedInProfile profile = new LinkedInProfile();
        for (final String attribute : OAuthAttributesDefinitions.linkedinDefinition.getAllAttributes()) {
            final String value = StringUtils.substringBetween(body, "<" + attribute + ">", "</" + attribute + ">");
            profile.addAttribute(attribute, value);
            if (LinkedInAttributesDefinition.URL.equals(attribute)) {
                final String id = StringUtils.substringBetween(value, "id=", "&amp;authType=");
                profile.setId(id);
            }
        }
        return profile;
    }
}
