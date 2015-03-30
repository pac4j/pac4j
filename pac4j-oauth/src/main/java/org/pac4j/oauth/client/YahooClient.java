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
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.yahoo.YahooProfile;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth10aServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>This class is the OAuth client to authenticate users in Yahoo.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.yahoo.YahooProfile}.</p>
 * <p>More information at http://developer.yahoo.com/social/rest_api_guide/extended-profile-resource.html</p>
 * 
 * @see org.pac4j.oauth.profile.yahoo.YahooProfile
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class YahooClient extends BaseOAuth10Client<YahooProfile> {
    
    public YahooClient() {
    }
    
    public YahooClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    @Override
    protected YahooClient newClient() {
        return new YahooClient();
    }
    
    @Override
    protected void internalInit() {
        super.internalInit();
        this.service = new ProxyOAuth10aServiceImpl(new YahooApi(), new OAuthConfig(this.key, this.secret,
                                                                                    this.callbackUrl,
                                                                                    SignatureType.Header, null, null),
                                                    this.connectTimeout, this.readTimeout, this.proxyHost,
                                                    this.proxyPort);
    }
    
    @Override
    protected String getProfileUrl(final Token accessToken) {
        return "https://social.yahooapis.com/v1/me/guid?format=xml";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected YahooProfile retrieveUserProfileFromToken(final Token accessToken) {
        // get the guid : https://developer.yahoo.com/social/rest_api_guide/introspective-guid-resource.html
        String body = sendRequestForData(accessToken, getProfileUrl(accessToken));
        final String guid = StringUtils.substringBetween(body, "<value>", "</value>");
        logger.debug("guid : {}", guid);
        if (StringUtils.isBlank(guid)) {
            final String message = "Cannot find guid from body : " + body;
            logger.error(message);
            throw new HttpCommunicationException(message);
        }
        body = sendRequestForData(accessToken, "https://social.yahooapis.com/v1/user/" + guid + "/profile?format=json");
        final YahooProfile profile = extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        return profile;
    }
    
    @Override
    protected YahooProfile extractUserProfile(final String body) {
        final YahooProfile profile = new YahooProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            json = json.get("profile");
            if (json != null) {
                profile.setId(JsonHelper.get(json, "guid"));
                for (final String attribute : OAuthAttributesDefinitions.yahooDefinition.getAllAttributes()) {
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
