package org.pac4j.oauth.client;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.paypal.PayPalProfileDefinition;
import org.pac4j.scribe.builder.api.PayPalApi20;

import java.util.Optional;

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
public class PayPalClient extends OAuth20Client {

    /** Constant <code>DEFAULT_SCOPE="openid profile email address"</code> */
    public final static String DEFAULT_SCOPE = "openid profile email address";

    /**
     * <p>Constructor for PayPalClient.</p>
     */
    public PayPalClient() {
        setScope(DEFAULT_SCOPE);
    }

    /**
     * <p>Constructor for PayPalClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public PayPalClient(final String key, final String secret) {
        setScope(DEFAULT_SCOPE);
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotBlank("scope", getConfiguration().getScope());
        configuration.setApi(new PayPalApi20());
        configuration.setProfileDefinition(new PayPalProfileDefinition());
        configuration.setTokenAsHeader(true);
        setLogoutActionBuilderIfUndefined((ctx, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx.webContext(), "https://www.paypal.com/myaccount/logout")));

        super.internalInit(forceReinit);
    }

    /**
     * <p>getScope.</p>
     *
     * @return a {@link String} object
     */
    public String getScope() {
        return getConfiguration().getScope();
    }

    /**
     * <p>setScope.</p>
     *
     * @param scope a {@link String} object
     */
    public void setScope(final String scope) {
        getConfiguration().setScope(scope);
    }
}
