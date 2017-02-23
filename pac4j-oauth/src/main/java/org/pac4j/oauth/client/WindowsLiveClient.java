package org.pac4j.oauth.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.oauth.profile.windowslive.WindowsLiveProfile;
import org.pac4j.oauth.profile.windowslive.WindowsLiveProfileDefinition;
import org.pac4j.scribe.builder.api.WindowsLiveApi20;

/**
 * <p>This class is the OAuth client to authenticate users in Windows Live (SkyDrive, Hotmail and Messenger).</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.windowslive.WindowsLiveProfile}.</p>
 * <p>More information at http://msdn.microsoft.com/en-us/library/live/hh243641.aspx</p>
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WindowsLiveClient extends OAuth20Client<WindowsLiveProfile> {

    public WindowsLiveClient() {
    }

    public WindowsLiveClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit(final WebContext context) {
        configuration.setApi(new WindowsLiveApi20());
        configuration.setProfileDefinition(new WindowsLiveProfileDefinition());
        configuration.setScope("wl.basic");
        configuration.setHasGrantType(true);
        setConfiguration(configuration);
        defaultLogoutActionBuilder((ctx, profile, targetUrl) -> RedirectAction.redirect("https://account.microsoft.com/auth/complete-signout"));

        super.clientInit(context);
    }
}
