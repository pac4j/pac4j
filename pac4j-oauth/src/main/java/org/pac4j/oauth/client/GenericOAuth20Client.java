package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.scribe.builder.api.GenericApi20;

/**
 * <p>This class is the OAuth client to authenticate users using OAuth protocol version 2.0.</p>
 * <p>It returns an OAuth 2.0 profile.</p>
 *
 * @author aherrick
 * @since 1.9.2
 */
@Deprecated
public class GenericOAuth20Client<U extends OAuth20Profile> extends BaseOAuth20Client<U> {

    protected String authUrl = null;
    protected String tokenUrl = null;
    protected String profileUrl = null;

    protected String scope = null;

    public GenericOAuth20Client() {
    }

    /**
     * Convenience constructor.
     *
     * @param key the key
     * @param secret the secret
     * @param authUrl the authorization url
     * @param tokenUrl the access token url
     * @param profileUrl the url to retrieve the profile
     * @param scope the OAuth scope
     */
    public GenericOAuth20Client(final String key,
                                final String secret,
                                final String authUrl,
                                final String tokenUrl,
                                final String profileUrl,
                                final String scope) {
        setKey(key);
        setSecret(secret);
        this.authUrl = authUrl;
        this.tokenUrl = tokenUrl;
        this.profileUrl = profileUrl;
        this.scope = scope;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("authEndpoint", this.authUrl);
        CommonHelper.assertNotBlank("tokenEndpoint", this.tokenUrl);
        CommonHelper.assertNotBlank("profileEndpoint", this.profileUrl);
        super.internalInit(context);
    }

    @Override
    protected BaseApi<OAuth20Service> getApi() {
        return new GenericApi20(authUrl, tokenUrl);
    }

    @Override
    protected String getProfileUrl(final OAuth2AccessToken accessToken) {
        return profileUrl;
    }

    @Override
    protected U extractUserProfile(String body) {
        final U profile = getProfileDefinition().newProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.getElement(json, "id"));
            for (final String attribute : getProfileDefinition().getPrimaryAttributes()) {
                getProfileDefinition().convertAndAdd(profile, attribute, JsonHelper.getElement(json, attribute));
            }
        }
        return (U) profile;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    protected String getOAuthScope() {
        return scope;
    }
}
