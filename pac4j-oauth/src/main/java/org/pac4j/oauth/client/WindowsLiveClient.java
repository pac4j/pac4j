package org.pac4j.oauth.client;

import com.github.scribejava.apis.LiveApi;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.windowslive.WindowsLiveProfileDefinition;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in Windows Live (SkyDrive, Hotmail and Messenger).</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.windowslive.WindowsLiveProfile}.</p>
 * <p>More information at http://msdn.microsoft.com/en-us/library/live/hh243641.aspx</p>
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WindowsLiveClient extends OAuth20Client {

    /**
     * <p>Constructor for WindowsLiveClient.</p>
     */
    public WindowsLiveClient() {
    }

    /**
     * <p>Constructor for WindowsLiveClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public WindowsLiveClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(LiveApi.instance());
        configuration.setProfileDefinition(new WindowsLiveProfileDefinition());
        configuration.setScope("wl.basic");
        setLogoutActionBuilderIfUndefined((ctx, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx.webContext(), "https://account.microsoft.com/auth/complete-signout")));

        super.internalInit(forceReinit);
    }
}
