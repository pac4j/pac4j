package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;
import org.pac4j.scribe.builder.api.CasOAuthWrapperApi20;

import java.util.Iterator;

/**
 * <p>This class is the OAuth client to authenticate users on CAS servers using OAuth wrapper.</p>
 * <p>The url of the OAuth endpoint of the CAS server must be set by using the {@link #setCasOAuthUrl(String)} method.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile}.</p>
 * <p>More information at https://wiki.jasig.org/display/CASUM/OAuth+server+support</p>
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class CasOAuthWrapperClient extends BaseOAuth20Client<CasOAuthWrapperProfile> {
    
    private String casOAuthUrl;
    
    private boolean springSecurityCompliant = false;

    private boolean implicitFlow = false;

    public CasOAuthWrapperClient() {
    }
    
    public CasOAuthWrapperClient(final String key, final String secret, final String casOAuthUrl) {
        setKey(key);
        setSecret(secret);
        this.casOAuthUrl = casOAuthUrl;
    }
    
    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("casOAuthUrl", this.casOAuthUrl);
        super.internalInit(context);
    }

    @Override
    protected BaseApi<OAuth20Service> getApi() {
        return new CasOAuthWrapperApi20(this.casOAuthUrl, this.springSecurityCompliant, this.implicitFlow);
    }

    @Override
    protected  boolean hasOAuthGrantType() {
        return true;
    }

    @Override
    protected String getProfileUrl(final OAuth2AccessToken accessToken) {
        return this.casOAuthUrl + "/profile";
    }
    
    @Override
    protected CasOAuthWrapperProfile extractUserProfile(final String body) throws HttpAction {
        final CasOAuthWrapperProfile userProfile = new CasOAuthWrapperProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            userProfile.setId(JsonHelper.getElement(json, "id"));
            json = json.get("attributes");
            if (json != null) {
                // CAS <= v4.2
                if (json instanceof ArrayNode) {
                    final Iterator<JsonNode> nodes = json.iterator();
                    while (nodes.hasNext()) {
                        json = nodes.next();
                        final String attribute = json.fieldNames().next();
                        userProfile.addAttribute(attribute, JsonHelper.getElement(json, attribute));
                    }
                    // CAS v5
                } else if (json instanceof ObjectNode) {
                    final Iterator<String> keys = json.fieldNames();
                    while (keys.hasNext()) {
                        final String key = keys.next();
                        userProfile.addAttribute(key, JsonHelper.getElement(json, key));
                    }
                }
            }
        }
        return userProfile;
    }
    
    public String getCasOAuthUrl() {
        return this.casOAuthUrl;
    }
    
    public void setCasOAuthUrl(final String casOAuthUrl) {
        this.casOAuthUrl = casOAuthUrl;
    }
    
    public boolean isSpringSecurityCompliant() {
        return this.springSecurityCompliant;
    }
    
    public void setSpringSecurityCompliant(final boolean springSecurityCompliant) {
        this.springSecurityCompliant = springSecurityCompliant;
    }

    public boolean isImplicitFlow() {
        return implicitFlow;
    }

    public void setImplicitFlow(final boolean implicitFlow) {
        this.implicitFlow = implicitFlow;
    }
}
