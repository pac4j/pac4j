package org.pac4j.core.engine;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.ProfileManagerFactoryAware;
import org.pac4j.core.redirect.RedirectAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;
import java.util.List;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>Default logout logic:</p>
 *
 * <p>If the <code>localLogout</code> property is <code>true</code>, the pac4j profiles are removed from the web session
 * (and the web session is destroyed if the <code>killSession</code> property is <code>true</code>).</p>
 *
 *  <p>A post logout action is computed as the redirection to the <code>url</code> request parameter if it matches the <code>logoutUrlPattern</code>
 * or to the <code>defaultUrl</code> if it is defined or as a blank page otherwise.</p>
 *
 *  <p>If the <code>centralLogout</code> property is <code>true</code>, the user is redirected to the identity provider
 * for a central logout and then optionally to the post logout redirection URL (if it's supported by the identity provider and if it's an absolute URL).
 * If no central logout is defined, the post logout action is performed directly.</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class DefaultLogoutLogic<R, C extends WebContext> extends ProfileManagerFactoryAware<C> implements LogoutLogic<R, C> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private boolean killSession;

    @Override
    public R perform(final C context, final Config config, final HttpActionAdapter<R, C> httpActionAdapter,
                     final String defaultUrl, final String inputLogoutUrlPattern, final Boolean inputLocalLogout,
                     final Boolean inputCentralLogout) {

        logger.debug("=== APP LOGOUT ===");

        // default values
        final String logoutUrlPattern;
        if (inputLogoutUrlPattern == null) {
            logoutUrlPattern = Pac4jConstants.DEFAULT_LOGOUT_URL_PATTERN_VALUE;
        } else {
            logoutUrlPattern = inputLogoutUrlPattern;
        }
        final boolean localLogout;
        if (inputLocalLogout == null) {
            localLogout = true;
        } else {
            localLogout = inputLocalLogout;
        }
        final boolean centralLogout;
        if (inputCentralLogout == null) {
            centralLogout = false;
        } else {
            centralLogout = inputCentralLogout;
        }

        // checks
        assertNotNull("context", context);
        assertNotNull("config", config);
        assertNotNull("httpActionAdapter", httpActionAdapter);
        assertNotBlank(Pac4jConstants.LOGOUT_URL_PATTERN, logoutUrlPattern);
        final Clients configClients = config.getClients();
        assertNotNull("configClients", configClients);

        // logic
        final ProfileManager manager = getProfileManager(context);
        final List<CommonProfile> profiles = manager.getAll(true);

        // compute redirection URL
        final String url = context.getRequestParameter(Pac4jConstants.URL);
        String redirectUrl = defaultUrl;
        if (url != null && Pattern.matches(logoutUrlPattern, url)) {
            redirectUrl = url;
        }
        logger.debug("redirectUrl: {}", redirectUrl);
        HttpAction action;
        if (redirectUrl != null) {
            action = HttpAction.redirect("redirect", context, redirectUrl);
        } else {
            action = HttpAction.ok("ok", context);
        }

        // local logout if requested or multiple profiles
        if (localLogout || profiles.size() > 1) {
            logger.debug("Performing application logout");
            manager.logout();
            postLogout(context);
        }

        // central logout
        if (centralLogout) {
            logger.debug("Performing central logout");
            for (final CommonProfile profile : profiles) {
                logger.debug("Profile: {}", profile);
                final String clientName = profile.getClientName();
                if (clientName != null) {
                    final Client client = configClients.findClient(clientName);
                    if(client != null) {
                        final String targetUrl;
                        if (redirectUrl != null && (redirectUrl.startsWith(HttpConstants.SCHEME_HTTP) || redirectUrl.startsWith(HttpConstants.SCHEME_HTTPS))) {
                            targetUrl = redirectUrl;
                        } else {
                            targetUrl = null;
                        }
                        final RedirectAction logoutAction = client.getLogoutAction(context, profile, targetUrl);
                        logger.debug("Logout action: {}", logoutAction);
                        if (logoutAction != null) {
                            action = logoutAction.perform(context);
                            break;
                        }
                    }
                }
            }
        }

        return httpActionAdapter.adapt(action.getCode(), context);
    }

    /**
     * Specific post logout action.
     *
     * @param context the web context
     */
    protected void postLogout(final C context) {
        if (this.killSession) {
            context.invalidationSession();
        }
    }

    public boolean isKillSession() {
        return killSession;
    }

    public void setKillSession(final boolean killSession) {
        this.killSession = killSession;
    }
}
