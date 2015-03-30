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
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.paypal.PayPalProfile;
import org.scribe.builder.api.PayPalApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.PayPalOAuth20ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>This class is the OAuth client to authenticate users in PayPal.</p>
 * <p>By default, the following <i>scope</i> is requested to PayPal : openid profile email address.</p>
 * <p>The <i>scope</i> can be defined to require permissions from the user and retrieve attributes from PayPal, by using the
 * {@link #setScope(String)} method.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.paypal.PayPalProfile}.</p>
 * <p>More information at https://developer.paypal.com/webapps/developer/docs/integration/direct/log-in-with-paypal/detailed/</p>
 * 
 * @see org.pac4j.oauth.profile.paypal.PayPalProfile
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalClient extends BaseOAuth20Client<PayPalProfile> {
    
    public final static String DEFAULT_SCOPE = "openid profile email address";
    
    protected String scope = DEFAULT_SCOPE;
    
    public PayPalClient() {
        setTokenAsHeader(true);
    }
    
    public PayPalClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
        setTokenAsHeader(true);
    }
    
    @Override
    protected PayPalClient newClient() {
        final PayPalClient newClient = new PayPalClient();
        newClient.setScope(this.scope);
        return newClient;
    }
    
    @Override
    protected void internalInit() {
        super.internalInit();
        CommonHelper.assertNotBlank("scope", this.scope);
        this.service = new PayPalOAuth20ServiceImpl(new PayPalApi20(), new OAuthConfig(this.key, this.secret,
                                                                                       this.callbackUrl,
                                                                                       SignatureType.Header,
                                                                                       this.scope, null),
                                                    this.connectTimeout, this.readTimeout, this.proxyHost,
                                                    this.proxyPort);
    }
    
    @Override
    protected String getProfileUrl(final Token accessToken) {
        return "https://api.paypal.com/v1/identity/openidconnect/userinfo?schema=openid";
    }
    
    @Override
    protected PayPalProfile extractUserProfile(final String body) {
        final PayPalProfile profile = new PayPalProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            final String userId = (String) JsonHelper.get(json, "user_id");
            profile.setId(StringUtils.substringAfter(userId, "/user/"));
            for (final String attribute : OAuthAttributesDefinitions.payPalDefinition.getAllAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
        }
        return profile;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public void setScope(final String scope) {
        this.scope = scope;
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
