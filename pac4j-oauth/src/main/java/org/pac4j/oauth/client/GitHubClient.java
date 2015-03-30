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
import org.pac4j.oauth.profile.github.GitHubProfile;
import org.scribe.builder.api.GitHubApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>This class is the OAuth client to authenticate users in GitHub.</p>
 * <p>The <i>scope</i> can be defined to require specific permissions from the user by using the {@link #setScope(String)} method. By default,
 * the <i>scope</i> is : <code>user</code>.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.github.GitHubProfile}.</p>
 * <p>More information at http://developer.github.com/v3/users/</p>
 * 
 * @see org.pac4j.oauth.profile.github.GitHubProfile
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class GitHubClient extends BaseOAuth20Client<GitHubProfile> {
    
    public final static String DEFAULT_SCOPE = "user";
    
    protected String scope = DEFAULT_SCOPE;
    
    public GitHubClient() {
    }
    
    public GitHubClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    @Override
    protected GitHubClient newClient() {
        GitHubClient client = new GitHubClient();
        client.setScope(this.scope);
        return client;
    }
    
    @Override
    protected void internalInit() {
        super.internalInit();
        this.service = new ProxyOAuth20ServiceImpl(new GitHubApi(), new OAuthConfig(this.key, this.secret,
                                                                                    this.callbackUrl,
                                                                                    SignatureType.Header, this.scope,
                                                                                    null), this.connectTimeout,
                                                   this.readTimeout, this.proxyHost, this.proxyPort);
    }
    
    @Override
    protected String getProfileUrl(final Token accessToken) {
        return "https://api.github.com/user";
    }
    
    @Override
    protected GitHubProfile extractUserProfile(final String body) {
        final GitHubProfile profile = new GitHubProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, "id"));
            for (final String attribute : OAuthAttributesDefinitions.githubDefinition.getAllAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
        }
        return profile;
    }
    
    @Override
    protected boolean requiresStateParameter() {
        return false;
    }
    
    @Override
    protected boolean hasBeenCancelled(final WebContext context) {
        return false;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public void setScope(final String scope) {
        this.scope = scope;
    }
}
