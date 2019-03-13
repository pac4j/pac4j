package org.pac4j.oauth.client;

import org.pac4j.core.exception.http.RedirectionActionHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.paypal.PayPalProfile;
import org.pac4j.oauth.profile.paypal.PayPalProfileDefinition;
import org.pac4j.scribe.builder.api.PayPalApi20;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in PayPal.</p>
 * <p>By default, the following <i>scope</i> is requested to PayPal : openid profile email address.</p>
 * <p>The <i>scope</i> can be defined to require permissions from the user and retrieve attributes from PayPal, by using the
 * {@link #setScope(String)} method.</p>
 * <p>It returns a {@link PayPalProfile}.</p>
 * <p>More information at https://developer.paypal.com/webapps/developer/docs/integration/direct/log-in-with-paypal/detailed/</p>
 *
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalClient extends OAuth20Client {

    public final static String DEFAULT_SCOPE = "openid profile email address";

    public PayPalClient() {
        setScope(DEFAULT_SCOPE);
    }

    public PayPalClient(final String key, final String secret) {
        setScope(DEFAULT_SCOPE);
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit() {
        CommonHelper.assertNotBlank("scope", getConfiguration().getScope());
        configuration.setApi(new PayPalApi20());
        configuration.setProfileDefinition(new PayPalProfileDefinition());
        configuration.setTokenAsHeader(true);
        defaultLogoutActionBuilder((ctx, profile, targetUrl) ->
            Optional.of(RedirectionActionHelper.buildRedirectUrlAction(ctx, "https://www.paypal.com/myaccount/logout")));

        super.clientInit();
    }

    public String getScope() {
        return getConfiguration().getScope();
    }

    public void setScope(final String scope) {
        getConfiguration().setScope(scope);
    }
}
