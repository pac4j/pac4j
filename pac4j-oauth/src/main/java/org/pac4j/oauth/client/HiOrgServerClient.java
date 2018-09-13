package org.pac4j.oauth.client;

import com.github.scribejava.apis.HiOrgServerApi20;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.hiorgserver.HiOrgServerConfiguration;
import org.pac4j.oauth.profile.hiorgserver.HiOrgServerProfile;
import org.pac4j.oauth.profile.hiorgserver.HiOrgServerProfileDefinition;

/**
 * This class is the OAuth client to authenticate users in HiOrg-Server.
 *
 * @author Martin Böhmer
 * @since 3.2.0
 */
public class HiOrgServerClient extends OAuth20Client<HiOrgServerProfile> {

    private final static String LOGOUT_URL = "https://www.hiorg-server.de/logout.php";

    public HiOrgServerClient() {
        configuration = new HiOrgServerConfiguration();
    }

    public HiOrgServerClient(final String key, final String secret) {
        configuration = new HiOrgServerConfiguration();
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit() {
        configuration.setApi(HiOrgServerApi20.instance());
        configuration.setProfileDefinition(new HiOrgServerProfileDefinition());
        configuration.setHasBeenCancelledFactory(ctx -> {
            final String error = ctx.getRequestParameter(OAuthCredentialsException.ERROR);
            final String errorDescription = ctx.getRequestParameter(OAuthCredentialsException.ERROR_DESCRIPTION);
            // user has denied permissions
            if ("access_denied".equals(error)) {
                logger.debug(errorDescription);
                return true;
            } else {
                return false;
            }
        });
        configuration.setWithState(true);
        defaultLogoutActionBuilder((ctx, profile, targetUrl) -> RedirectAction.redirect(LOGOUT_URL));

        super.clientInit();
    }

}
