package org.pac4j.oauth.client;

import com.github.scribejava.apis.YahooApi;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.yahoo.YahooProfileCreator;
import org.pac4j.oauth.profile.yahoo.YahooProfileDefinition;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in Yahoo.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.yahoo.YahooProfile}.</p>
 * <p>More information at http://developer.yahoo.com/social/rest_api_guide/extended-profile-resource.html</p>
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class YahooClient extends OAuth10Client {

    /**
     * <p>Constructor for YahooClient.</p>
     */
    public YahooClient() {
    }

    /**
     * <p>Constructor for YahooClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public YahooClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(YahooApi.instance());
        configuration.setProfileDefinition(new YahooProfileDefinition());
        setProfileCreatorIfUndefined(new YahooProfileCreator(configuration, this));
        setLogoutActionBuilderIfUndefined((ctx, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx.webContext(), "http://login.yahoo.com/config/login?logout=1")));

        super.internalInit(forceReinit);
    }
}
