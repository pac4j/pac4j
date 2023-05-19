package org.pac4j.oauth.client;

import com.github.scribejava.apis.HiOrgServerApi20;
import lombok.val;
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

    /**
     * <p>Constructor for HiOrgServerClient.</p>
     */
    public HiOrgServerClient() {
        configuration = new HiOrgServerConfiguration();
    }

    /**
     * <p>Constructor for HiOrgServerClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public HiOrgServerClient(final String key, final String secret) {
        configuration = new HiOrgServerConfiguration();
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(HiOrgServerApi20.instance());
        configuration.setProfileDefinition(new HiOrgServerProfileDefinition());
        configuration.setHasBeenCancelledFactory(ctx -> {
            val error = ctx.getRequestParameter(OAuthCredentialsException.ERROR).orElse(null);
            val errorDescription = ctx.getRequestParameter(OAuthCredentialsException.ERROR_DESCRIPTION).orElse(null);
            // user has denied authorizations
            if ("access_denied".equals(error)) {
                logger.debug(errorDescription);
                return true;
            } else {
                return false;
            }
        });
        configuration.setWithState(true);
        setLogoutActionBuilderIfUndefined((ctx, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx.webContext(), LOGOUT_URL)));

        super.internalInit(forceReinit);
    }

}
