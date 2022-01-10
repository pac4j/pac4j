package org.pac4j.oauth.client;

import com.github.scribejava.apis.HiOrgServerApi20;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.hiorgserver.HiOrgServerConfiguration;
import org.pac4j.oauth.profile.hiorgserver.HiOrgServerProfileDefinition;

import java.util.Optional;

/**
 * This class is the OAuth client to authenticate users in HiOrg-Server.
 *
 * @author Martin Böhmer
 * @since 3.2.0
 */
public class HiOrgServerClient extends OAuth20Client {

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
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(HiOrgServerApi20.instance());
        configuration.setProfileDefinition(new HiOrgServerProfileDefinition());
        configuration.setHasBeenCancelledFactory(ctx -> {
            final var error = ctx.getRequestParameter(OAuthCredentialsException.ERROR).orElse(null);
            final var errorDescription = ctx.getRequestParameter(OAuthCredentialsException.ERROR_DESCRIPTION).orElse(null);
            // user has denied permissions
            if ("access_denied".equals(error)) {
                logger.debug(errorDescription);
                return true;
            } else {
                return false;
            }
        });
        configuration.setWithState(true);
        defaultLogoutActionBuilder((ctx, session, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx, LOGOUT_URL)));

        super.internalInit(forceReinit);
    }

}
