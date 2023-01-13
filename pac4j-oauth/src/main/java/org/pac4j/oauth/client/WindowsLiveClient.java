package org.pac4j.oauth.client;

import com.github.scribejava.apis.LiveApi;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.windowslive.WindowsLiveProfile;
import org.pac4j.oauth.profile.windowslive.WindowsLiveProfileDefinition;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in Windows Live (SkyDrive, Hotmail and Messenger).</p>
 * <p>It returns a {@link WindowsLiveProfile}.</p>
 * <p>More information at http://msdn.microsoft.com/en-us/library/live/hh243641.aspx</p>
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WindowsLiveClient extends OAuth20Client {

    public WindowsLiveClient() {
    }

    public WindowsLiveClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(LiveApi.instance());
        configuration.setProfileDefinition(new WindowsLiveProfileDefinition());
        configuration.setScope("wl.basic");
        setLogoutActionBuilderIfUndefined((ctx, session, pmf, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx, "https://account.microsoft.com/auth/complete-signout")));

        super.internalInit(forceReinit);
    }
}
