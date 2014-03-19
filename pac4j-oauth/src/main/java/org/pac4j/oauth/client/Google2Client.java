/*
  Copyright 2012 - 2014 Jerome Leleu

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
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.google2.Google2Profile;
import org.scribe.builder.api.GoogleApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is the OAuth client to authenticate users in Google using OAuth protocol version 2.0.
 * <p />
 * The <i>scope</i> is by default : {@link Google2Scope#EMAIL}, but it can also but set to : {@link Google2Scope#PROFILE} or
 * {@link Google2Scope#EMAIL_AND_PROFILE}.
 * <p />
 * It returns a {@link org.pac4j.oauth.profile.google2.Google2Profile}.
 * <p />
 * More information at https://developers.google.com/accounts/docs/OAuth2Login
 *
 * @see org.pac4j.oauth.profile.google2.Google2Profile
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2Client extends BaseOAuth20Client<Google2Profile> {

    public enum Google2Scope {
        EMAIL,
        PROFILE,
        EMAIL_AND_PROFILE
    };

    protected final String PROFILE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";

    protected final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";

    protected Google2Scope scope = Google2Scope.EMAIL_AND_PROFILE;

    protected String scopeValue;

    public Google2Client() {
    }

    public Google2Client(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected Google2Client newClient() {
        final Google2Client newClient = new Google2Client();
        newClient.setScope(this.scope);
        return newClient;
    }

    @Override
    protected void internalInit() {
        super.internalInit();
        CommonHelper.assertNotNull("scope", this.scope);
        if (this.scope == Google2Scope.EMAIL) {
            this.scopeValue = this.EMAIL_SCOPE;
        } else if (this.scope == Google2Scope.PROFILE) {
            this.scopeValue = this.PROFILE_SCOPE;
        } else {
            this.scopeValue = this.PROFILE_SCOPE + " " + this.EMAIL_SCOPE;
        }
        this.service = new ProxyOAuth20ServiceImpl(new GoogleApi20(), new OAuthConfig(this.key, this.secret,
                this.callbackUrl,
                SignatureType.Header,
                this.scopeValue, null),
                this.connectTimeout, this.readTimeout, this.proxyHost,
                this.proxyPort, false, true);
    }

    @Override
    protected String getProfileUrl(final Token accessToken) {
        return "https://www.googleapis.com/oauth2/v2/userinfo";
    }

    @Override
    protected Google2Profile extractUserProfile(final String body) {
        final Google2Profile profile = new Google2Profile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, "id"));
            for (final String attribute : OAuthAttributesDefinitions.google2Definition.getPrincipalAttributes()) {
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
    }

    @Override
    protected boolean requiresStateParameter() {
        return false;
    }

    @Override
    protected boolean hasBeenCancelled(final WebContext context) {
        final String error = context.getRequestParameter(OAuthCredentialsException.ERROR);
        // user has denied permissions
        if ("access_denied".equals(error)) {
            return true;
        } else {
            return false;
        }
    }
}
