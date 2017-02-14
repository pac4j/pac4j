package org.pac4j.oauth.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.paypal.PayPalProfile;
import org.pac4j.oauth.profile.paypal.PayPalProfileDefinition;
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
public class PayPalClient extends OAuth20Client<PayPalProfile> {
    
    public final static String DEFAULT_SCOPE = "openid profile email address";
    
    protected String scope = DEFAULT_SCOPE;
    
    public PayPalClient() {
    }
    
    public PayPalClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    @Override
    protected void clientInit(final WebContext context) {
        CommonHelper.assertNotBlank("scope", this.scope);
        configuration.setApi(new PayPalApi20());
        configuration.setProfileDefinition(new PayPalProfileDefinition());
        configuration.setScope(this.scope);
        configuration.setHasGrantType(true);
        configuration.setTokenAsHeader(true);
        setConfiguration(configuration);
        defaultLogoutActionBuilder((ctx, profile, targetUrl) -> RedirectAction.redirect("https://www.paypal.com/myaccount/logout"));

        super.clientInit(context);
    }

    public String getScope() {
        return this.scope;
    }
    
    public void setScope(final String scope) {
        this.scope = scope;
    }
}
