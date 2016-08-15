package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.generic.GenericAttributesDefinition;
import org.pac4j.oauth.profile.generic.GenericOAuth20Profile;
import org.pac4j.scribe.builder.api.GenericApi20;

/**
 * <p>This class is the OAuth client to authenticate users using OAuth protocol version 2.0.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.generic.GenericOAuth20Profile}.</p>
 *
 * @author aherrick
 * @since 1.9.2
 */
public class GenericOAuth20Client extends BaseOAuth20Client<GenericOAuth20Profile> {

    protected String baseUrl = null;
    protected String authEndpoint = null;
    protected String tokenEndpoint = null;
    protected String profileEndpoint = null;
    protected GenericAttributesDefinition attributesDefinition = null;

    protected String scope = null;

    public GenericOAuth20Client() {
    }

    /**
     * Convenience constructor. Uses {@link org.pac4j.oauth.profile.generic.GenericAttributesDefinition}
     * for the attributes definition
     */
    public GenericOAuth20Client(final String key,
                                final String secret,
                                final String baseUrl,
                                final String authEndpoint,
                                final String tokenEndpoint,
                                final String profileEndpoint,
                                final String scope) {
        setKey(key);
        setSecret(secret);
        this.baseUrl = baseUrl;
        this.authEndpoint = authEndpoint;
        this.tokenEndpoint = tokenEndpoint;
        this.profileEndpoint = profileEndpoint;
        this.scope = scope;
    }

    /**
     * Convenience constructor. Allows for a user-defined GenericAttributesDefinition to be passed in.
     * See {@link org.pac4j.oauth.profile.generic.GenericAttributesDefinition}
     */
    public GenericOAuth20Client(final String key,
                                final String secret,
                                final String baseUrl,
                                final String authEndpoint,
                                final String tokenEndpoint,
                                final String profileEndpoint,
                                final String scope,
                                final GenericAttributesDefinition attributes) {
        setKey(key);
        setSecret(secret);
        this.baseUrl = baseUrl;
        this.authEndpoint = authEndpoint;
        this.tokenEndpoint = tokenEndpoint;
        this.profileEndpoint = profileEndpoint;
        this.scope = scope;
        this.attributesDefinition = attributes;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("baseUrl", this.baseUrl);
        CommonHelper.assertNotBlank("authEndpoint", this.authEndpoint);
        CommonHelper.assertNotBlank("tokenEndpoint", this.tokenEndpoint);
        CommonHelper.assertNotBlank("profileEndpoint", this.profileEndpoint);
        CommonHelper.assertNotNull("scope", this.scope);
        super.internalInit(context);
    }

    @Override
    protected BaseApi<OAuth20Service> getApi() {
        return new GenericApi20(baseUrl, authEndpoint, tokenEndpoint);
    }

    @Override
    protected String getOAuthScope() {
        return this.scope;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    @Override
    protected String getProfileUrl(final OAuth2AccessToken accessToken) {
        return baseUrl + profileEndpoint;
    }

    @Override
    protected GenericOAuth20Profile extractUserProfile(String body) {
        final GenericOAuth20Profile profile = new GenericOAuth20Profile();
        if (attributesDefinition != null) {
            profile.setAttributesDefinition(attributesDefinition);
        }
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.getElement(json, "id"));
            for (final String attribute : profile.getAttributesDefinition().getPrimaryAttributes()) {
                profile.addAttribute(attribute, JsonHelper.getElement(json, attribute));
            }
        }
        return profile;
    }
}