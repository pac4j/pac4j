package org.pac4j.oauth.client;

import com.github.scribejava.apis.YahooApi;
import org.pac4j.core.exception.http.RedirectionActionHelper;
import org.pac4j.oauth.profile.yahoo.YahooProfile;
import org.pac4j.oauth.profile.yahoo.YahooProfileCreator;
import org.pac4j.oauth.profile.yahoo.YahooProfileDefinition;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in Yahoo.</p>
 * <p>It returns a {@link YahooProfile}.</p>
 * <p>More information at http://developer.yahoo.com/social/rest_api_guide/extended-profile-resource.html</p>
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class YahooClient extends OAuth10Client {

    public YahooClient() {
    }

    public YahooClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit() {
        configuration.setApi(YahooApi.instance());
        configuration.setProfileDefinition(new YahooProfileDefinition());
        defaultProfileCreator(new YahooProfileCreator(configuration, this));
        defaultLogoutActionBuilder((ctx, profile, targetUrl) ->
            Optional.of(RedirectionActionHelper.buildRedirectUrlAction(ctx, "http://login.yahoo.com/config/login?logout=1")));

        super.clientInit();
    }
}
