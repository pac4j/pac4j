package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.apis.LinkedInApi20;
import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2AttributesDefinition;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile;

/**
 * <p>This class is the OAuth client to authenticate users in LinkedIn (using OAuth 2.0 protocol).</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile}.</p>
 * <p>The scope (by default : <code>r_fullprofile</code>) can be specified using the {@link #setScope(String)} method, as well as the returned
 * fields through the {@link #setFields(String)} method.</p>
 * <p>More information at https://developer.linkedin.com/documents/profile-api</p>
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2Client extends BaseOAuth20StateClient<LinkedIn2Profile> {
    
    public final static String DEFAULT_SCOPE = "r_fullprofile";
    
    protected String scope = DEFAULT_SCOPE;
    
    protected String fields = "id,first-name,last-name,maiden-name,formatted-name,phonetic-first-name,phonetic-last-name,formatted-phonetic-name,headline,location,industry,current-share,num-connections,num-connections-capped,summary,specialties,positions,picture-url,site-standard-profile-request,api-standard-profile-request,public-profile-url,email-address";
    
    public LinkedIn2Client() {
    }
    
    public LinkedIn2Client(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("scope", this.scope);
        CommonHelper.assertNotBlank("fields", this.fields);
        super.internalInit(context);
    }

    @Override
    protected BaseApi<OAuth20Service> getApi() {
        return LinkedInApi20.instance();
    }

    @Override
    protected String getOAuthScope() {
        return this.scope;
    }

    @Override
    protected boolean hasBeenCancelled(final WebContext context) {
        final String error = context.getRequestParameter(OAuthCredentialsException.ERROR);
        final String errorDescription = context.getRequestParameter(OAuthCredentialsException.ERROR_DESCRIPTION);
        // user has denied permissions
        if ("access_denied".equals(error)
            && ("the+user+denied+your+request".equals(errorDescription) || "the user denied your request"
                .equals(errorDescription))) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected String getProfileUrl(final OAuth2AccessToken accessToken) {
        return "https://api.linkedin.com/v1/people/~:(" + this.fields + ")?format=json";
    }
    
    @Override
    protected LinkedIn2Profile extractUserProfile(final String body) throws HttpAction {
        LinkedIn2Profile profile = new LinkedIn2Profile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        profile.setId(JsonHelper.getElement(json, "id"));
        for (final String attribute : profile.getAttributesDefinition().getPrimaryAttributes()) {
            profile.addAttribute(attribute, JsonHelper.getElement(json, attribute));
        }
        final Object positions = JsonHelper.getElement(json, LinkedIn2AttributesDefinition.POSITIONS);
        if (positions != null && positions instanceof JsonNode) {
            profile.addAttribute(LinkedIn2AttributesDefinition.POSITIONS, JsonHelper.getElement((JsonNode) positions, "values"));
        }
        addUrl(profile, json, LinkedIn2AttributesDefinition.SITE_STANDARD_PROFILE_REQUEST);
        addUrl(profile, json, LinkedIn2AttributesDefinition.API_STANDARD_PROFILE_REQUEST);
        return profile;
    }

    private void addUrl(final LinkedIn2Profile profile, final JsonNode json, final String name) {
        final String url = (String) JsonHelper.getElement(json, name + ".url");
        profile.addAttribute(name, url);
    }

    public String getScope() {
        return this.scope;
    }
    
    public void setScope(final String scope) {
        this.scope = scope;
    }
    
    public String getFields() {
        return this.fields;
    }
    
    public void setFields(final String fields) {
        this.fields = fields;
    }
}
