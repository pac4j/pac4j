package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.paypal.PayPalProfile;
import org.pac4j.scribe.builder.api.PayPalApi20;

/**
 * <p>This class is the OAuth client to authenticate users in PayPal.</p>
 * <p>By default, the following <i>scope</i> is requested to PayPal : openid profile email address.</p>
 * <p>The <i>scope</i> can be defined to require permissions from the user and retrieve attributes from PayPal, by using the
 * {@link #setScope(String)} method.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.paypal.PayPalProfile}.</p>
 * <p>More information at https://developer.paypal.com/webapps/developer/docs/integration/direct/log-in-with-paypal/detailed/</p>
 * 
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
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("scope", this.scope);
        super.internalInit(context);
    }

    @Override
    protected BaseApi<OAuth20Service> getApi() {
        return new PayPalApi20();
    }

    @Override
    protected String getOAuthScope() {
        return this.scope;
    }

    @Override
    protected  boolean hasOAuthGrantType() {
        return true;
    }

    @Override
    protected String getProfileUrl(final OAuth2AccessToken accessToken) {
        return "https://api.paypal.com/v1/identity/openidconnect/userinfo?schema=openid";
    }
    
    @Override
    protected PayPalProfile extractUserProfile(final String body) throws HttpAction {
        final PayPalProfile profile = new PayPalProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            final String userId = (String) JsonHelper.getElement(json, "user_id");
            profile.setId(CommonHelper.substringAfter(userId, "/user/"));
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
