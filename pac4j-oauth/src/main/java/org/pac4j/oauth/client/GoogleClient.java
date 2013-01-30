/*
  Copyright 2012 - 2013 Jerome Leleu

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
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.google.GoogleProfile;
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth10aServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is the OAuth client to authenticate users in Google using OAuth protocol version 1.0.
 * <p />
 * It returns a {@link org.pac4j.oauth.profile.google.GoogleProfile}.
 * <p />
 * More information at http://code.google.com/intl/fr-FR/apis/contacts/docs/poco/1.0/developers_guide.html
 * 
 * @see org.pac4j.oauth.profile.google.GoogleProfile
 * @author Jerome Leleu
 * @since 1.0.0
 */
@Deprecated
public class GoogleClient extends BaseOAuth10Client<GoogleProfile> {
    
    public GoogleClient() {
    }
    
    public GoogleClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    @Override
    protected GoogleClient newClient() {
        return new GoogleClient();
    }
    
    @Override
    protected void internalInit() throws TechnicalException {
        super.internalInit();
        this.service = new ProxyOAuth10aServiceImpl(
                                                    new GoogleApi(),
                                                    new OAuthConfig(
                                                                    this.key,
                                                                    this.secret,
                                                                    this.callbackUrl,
                                                                    SignatureType.Header,
                                                                    "http://www-opensocial.googleusercontent.com/api/people/",
                                                                    null), this.proxyHost, this.proxyPort);
    }
    
    @Override
    public String retrieveRedirectionUrl(final WebContext context) {
        final Token requestToken = this.service.getRequestToken();
        logger.debug("requestToken : {}", requestToken);
        // save requestToken in session
        context.setSessionAttribute(getRequestTokenSessionAttributeName(), requestToken);
        final String authorizationUrl = "https://www.google.com/accounts/OAuthAuthorizeToken?oauth_token="
                                        + requestToken.getToken();
        logger.debug("authorizationUrl : {}", authorizationUrl);
        return authorizationUrl;
    }
    
    @Override
    protected String getProfileUrl() {
        return "http://www-opensocial.googleusercontent.com/api/people/@me/@self";
    }
    
    @Override
    protected GoogleProfile extractUserProfile(final String body) {
        final GoogleProfile profile = new GoogleProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            json = json.get("entry");
            if (json != null) {
                profile.setId(JsonHelper.get(json, "id"));
                for (final String attribute : OAuthAttributesDefinitions.googleDefinition.getPrincipalAttributes()) {
                    profile.addAttribute(attribute, JsonHelper.get(json, attribute));
                }
                json = json.get("name");
                if (json != null) {
                    for (final String attribute : OAuthAttributesDefinitions.googleDefinition.getOtherAttributes()) {
                        profile.addAttribute(attribute, JsonHelper.get(json, attribute));
                    }
                }
            }
        }
        return profile;
    }
}
