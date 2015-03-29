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
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.dropbox.DropBoxProfile;
import org.scribe.builder.api.DropBoxApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth10aServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>This class is the OAuth client to authenticate users in DropBox.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.dropbox.DropBoxProfile}.</p>
 * <p>More information at https://www.dropbox.com/developers/reference/api#account-info</p>
 * 
 * @see org.pac4j.oauth.profile.dropbox.DropBoxProfile
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxClient extends BaseOAuth10Client<DropBoxProfile> {
    
    public DropBoxClient() {
    }
    
    public DropBoxClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    @Override
    protected DropBoxClient newClient() {
        return new DropBoxClient();
    }
    
    @Override
    protected void internalInit() {
        super.internalInit();
        this.service = new ProxyOAuth10aServiceImpl(new DropBoxApi(),
                                                    new OAuthConfig(this.key, this.secret, this.callbackUrl,
                                                                    SignatureType.Header, null, null),
                                                    this.connectTimeout, this.readTimeout, this.proxyHost,
                                                    this.proxyPort);
    }
    
    @Override
    protected String getProfileUrl(final Token accessToken) {
        return "https://api.dropbox.com/1/account/info";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected OAuthCredentials getOAuthCredentials(final WebContext context) {
        // get tokenRequest from session
        final Token tokenRequest = (Token) context.getSessionAttribute(getRequestTokenSessionAttributeName());
        logger.debug("tokenRequest : {}", tokenRequest);
        // don't get parameters from url
        // token and verifier are equals and extracted from saved request token
        final String token = tokenRequest.getToken();
        logger.debug("token = verifier : {}", token);
        return new OAuthCredentials(tokenRequest, token, token, getName());
    }
    
    @Override
    protected DropBoxProfile extractUserProfile(final String body) {
        final DropBoxProfile profile = new DropBoxProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, "uid"));
            for (final String attribute : OAuthAttributesDefinitions.dropBoxDefinition.getPrincipalAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
            json = (JsonNode) JsonHelper.get(json, "quota_info");
            if (json != null) {
                for (final String attribute : OAuthAttributesDefinitions.dropBoxDefinition.getOtherAttributes()) {
                    profile.addAttribute(attribute, JsonHelper.get(json, attribute));
                }
            }
        }
        return profile;
    }
    
    @Override
    protected boolean hasBeenCancelled(final WebContext context) {
        return false;
    }
}
