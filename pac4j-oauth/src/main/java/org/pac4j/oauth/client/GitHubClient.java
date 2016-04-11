package org.pac4j.oauth.client;

import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.api.Api;
import com.github.scribejava.core.model.Token;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.github.GitHubProfile;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>This class is the OAuth client to authenticate users in GitHub.</p>
 * <p>The <i>scope</i> can be defined to require specific permissions from the user by using the {@link #setScope(String)} method. By default,
 * the <i>scope</i> is: <code>user</code>.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.github.GitHubProfile}.</p>
 * <p>More information at http://developer.github.com/v3/users/</p>
 * 
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
    protected Api getApi() {
        return GitHubApi.instance();
    }

    @Override
    protected String getOAuthScope() {
        return this.scope;
    }

    @Override
    protected String getProfileUrl(final Token accessToken) {
        return "https://api.github.com/user";
    }
    
    @Override
    protected GitHubProfile extractUserProfile(final String body) throws RequiresHttpAction {
        final GitHubProfile profile = new GitHubProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.getElement(json, "id"));
            for (final String attribute : profile.getAttributesDefinition().getPrimaryAttributes()) {
                profile.addAttribute(attribute, JsonHelper.getElement(json, attribute));
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
}
