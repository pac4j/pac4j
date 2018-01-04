package org.pac4j.oauth.client;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.twitter.TwitterProfile;
import org.pac4j.oauth.profile.twitter.TwitterProfileDefinition;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in Twitter.</p>
 * <p>You can define if a screen should always been displayed for authorization confirmation by using the
 * {@link #setAlwaysConfirmAuthorization(boolean)} method (<code>false</code> by default).</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.twitter.TwitterProfile}.</p>
 * <p>More information at https://dev.twitter.com/docs/api/1/get/account/verify_credentials</p>
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TwitterClient extends OAuth10Client<TwitterProfile> {

    private boolean alwaysConfirmAuthorization = false;

    public TwitterClient() {
    }

    public TwitterClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit() {
        configuration.setApi(getApi());
        configuration.setProfileDefinition(new TwitterProfileDefinition());
        configuration.setHasBeenCancelledFactory(ctx -> {
            final Optional<String> denied = ctx.getRequestParameter("denied");
            return denied.map(d -> CommonHelper.isNotBlank(d)).orElse(false);
        });
        defaultLogoutActionBuilder((ctx, profile, targetUrl) -> Optional.of(RedirectAction.redirect("https://twitter.com/logout")));

        super.clientInit();
    }

    protected BaseApi<OAuth10aService> getApi() {
        final DefaultApi10a api;
        if (this.alwaysConfirmAuthorization == false) {
            api = TwitterApi.Authenticate.instance();
        } else {
            api = TwitterApi.instance();
        }
        return api;
    }

    public boolean isAlwaysConfirmAuthorization() {
        return this.alwaysConfirmAuthorization;
    }

    public void setAlwaysConfirmAuthorization(final boolean alwaysConfirmAuthorization) {
        this.alwaysConfirmAuthorization = alwaysConfirmAuthorization;
    }
}
