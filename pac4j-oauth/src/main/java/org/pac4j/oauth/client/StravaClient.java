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
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.strava.StravaAttributesDefinition;
import org.pac4j.oauth.profile.strava.StravaProfile;
import org.scribe.builder.api.StravaApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>OAuth20Client for Strava.</p>
 * <p>Use the key as the client_id and secret as the client_secret, both provided by Strava at: <a href="https://www.strava.com/settings/api">https://www.strava.com/settings/api</a> </p>
 * <p>Set approvalPrompt to "force" if you want to force the authorization dialog to always display on Strava, otherwise let it to "auto" (default value). </p>
 * <p>More info at: <a href="http://strava.github.io/api/">http://strava.github.io/api/</a></p>
 *
 * @author Adrian Papusoi
 * @since 1.7.0
 */
public class StravaClient extends BaseOAuth20Client<StravaProfile> {

    /**
     * comma delimited string of ‘view_private’ and/or ‘write’, leave blank for read-only permissions.
     */
    protected String scope = null;
    /**
     * approvalPrompt is by default "auto".   <br>
     * If "force", then the authorization dialog is always displayed by Strava.
     */
    private String approvalPrompt = "auto";

    public StravaClient() {
    }

    public StravaClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    public String getApprovalPrompt() {
        return approvalPrompt;
    }

    public void setApprovalPrompt(String approvalPrompt) {
        this.approvalPrompt = approvalPrompt;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    protected StravaClient newClient() {
        StravaClient client = new StravaClient();
        client.setScope(scope);
        return client;
    }

    @Override
    protected void internalInit() {
        super.internalInit();
        service = new ProxyOAuth20ServiceImpl(new StravaApi(approvalPrompt), new OAuthConfig(key, secret, callbackUrl,
                SignatureType.Header, scope, null), connectTimeout, readTimeout, proxyHost, proxyPort, false, false);
    }

    @Override
    protected boolean requiresStateParameter() {
        return false;
    }

    @Override
    protected boolean hasBeenCancelled(WebContext context) {
        return false;
    }

    @Override
    protected String getProfileUrl(Token accessToken) {
        return "https://www.strava.com/api/v3/athlete";
    }

    @Override
    protected StravaProfile extractUserProfile(String body) {
        final StravaProfile profile = new StravaProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, StravaAttributesDefinition.ID));
            for (final String attribute : OAuthAttributesDefinitions.stravaDefinition.getAllAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
        }
        return profile;
    }

    @Override
    protected boolean isDirectRedirection() {
        return false;
    }
}
