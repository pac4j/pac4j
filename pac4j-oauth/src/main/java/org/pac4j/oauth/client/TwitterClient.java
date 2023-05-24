package org.pac4j.oauth.client;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import lombok.val;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.twitter.TwitterProfileDefinition;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in Twitter.</p>
 * <p>You can define if a screen should always been displayed for authorization confirmation by using the
 * {@link #setAlwaysConfirmAuthorization(boolean)} method (<code>false</code> by default).</p>
 * <p>If your twitter oauth app allows requests for email addresses you can enable requesting an email
 * address by using the {@link #setIncludeEmail(boolean)} method (<code>false</code> by default).</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.twitter.TwitterProfile}.</p>
 * <p>More information at https://dev.twitter.com/docs/api/1/get/account/verify_credentials</p>
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TwitterClient extends OAuth10Client {

    private boolean alwaysConfirmAuthorization = false;

    private boolean includeEmail = false;

    /**
     * <p>Constructor for TwitterClient.</p>
     */
    public TwitterClient() {}

    /**
     * <p>Constructor for TwitterClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public TwitterClient(final String key, final String secret) {
        this(key, secret, false);
    }

    /**
     * <p>Constructor for TwitterClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     * @param includeEmail a boolean
     */
    public TwitterClient(final String key, final String secret, boolean includeEmail) {
        setKey(key);
        setSecret(secret);
        this.includeEmail = includeEmail;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(getApi());
        configuration.setProfileDefinition(new TwitterProfileDefinition(includeEmail));
        configuration.setHasBeenCancelledFactory(ctx -> {
            val denied = ctx.getRequestParameter("denied");
            if (denied.isPresent()) {
                return true;
            } else {
                return false;
            }
        });
        setLogoutActionBuilderIfUndefined((ctx, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx.webContext(), "https://twitter.com/logout")));

        super.internalInit(forceReinit);
    }

    /**
     * <p>getApi.</p>
     *
     * @return a {@link DefaultApi10a} object
     */
    protected DefaultApi10a getApi() {
        final DefaultApi10a api;
        if (this.alwaysConfirmAuthorization == false) {
            api = TwitterApi.Authenticate.instance();
        } else {
            api = TwitterApi.instance();
        }
        return api;
    }

    /**
     * <p>isAlwaysConfirmAuthorization.</p>
     *
     * @return a boolean
     */
    public boolean isAlwaysConfirmAuthorization() {
        return this.alwaysConfirmAuthorization;
    }

    /**
     * <p>Setter for the field <code>alwaysConfirmAuthorization</code>.</p>
     *
     * @param alwaysConfirmAuthorization a boolean
     */
    public void setAlwaysConfirmAuthorization(final boolean alwaysConfirmAuthorization) {
        this.alwaysConfirmAuthorization = alwaysConfirmAuthorization;
    }

    /**
     * <p>isIncludeEmail.</p>
     *
     * @return a boolean
     */
    public boolean isIncludeEmail() {
        return includeEmail;
    }

    /**
     * <p>Setter for the field <code>includeEmail</code>.</p>
     *
     * @param includeEmail a boolean
     */
    public void setIncludeEmail(final boolean includeEmail) {
        this.includeEmail = includeEmail;
    }
}
