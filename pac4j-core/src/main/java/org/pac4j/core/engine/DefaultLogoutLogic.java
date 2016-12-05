package org.pac4j.core.engine;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.logout.LogoutRequest;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.ProfileManagerFactoryAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>Default logout logic:</p>
 * <p>The pac4j local logout is performed first and the whole web session is destroyed if the <code>killSession</code> property is <code>true</code>.</p>
 * <p>Then the central logout is performed for all clients which have produced the authenticated user profiles if the <code>centralLogout</code> property is <code>true</code>.</p>
 * <p>After logout, the user is redirected to the url defined by the <code>url</code> request parameter if it matches the <code>logoutUrlPattern</code>.
 * Or the user is redirected to the <code>defaultUrl</code> if it is defined. Otherwise, a blank page is displayed.</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class DefaultLogoutLogic<R, C extends WebContext> extends ProfileManagerFactoryAware<C> implements LogoutLogic<R, C> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private boolean killSession;

    @Override
    public R perform(final C context, final Config config, final HttpActionAdapter<R, C> httpActionAdapter,
                       final String defaultUrl, final String inputLogoutUrlPattern, final Boolean inputCentralLogout) {

        logger.debug("=== APP LOGOUT ===");

        // default values
        final String logoutUrlPattern;
        if (inputLogoutUrlPattern == null) {
            logoutUrlPattern = Pac4jConstants.DEFAULT_LOGOUT_URL_PATTERN_VALUE;
        } else {
            logoutUrlPattern = inputLogoutUrlPattern;
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
        logger.debug("Performing application logout");
        manager.logout();
        if (centralLogout) {
            logger.debug("Performing central logout");
            for (final CommonProfile profile : profiles) {
                logger.debug("Profile: {}", profile);
                final String clientName = profile.getClientName();
                if (isNotBlank(clientName)) {
                    final Client client = configClients.findClient(clientName);
                    final LogoutRequest logoutRequest = client.getLogoutRequest(context, profile);
                    logger.debug("Logout request: {}", logoutRequest);
                    executeLogoutRequest(context, logoutRequest);
                }
            }
        }
        postLogout(context);

        final String url = context.getRequestParameter(Pac4jConstants.URL);
        String redirectUrl = defaultUrl;
        if (url != null && Pattern.matches(logoutUrlPattern, url)) {
            redirectUrl = url;
        }
        logger.debug("redirectUrl: {}", redirectUrl);
        final HttpAction action;
        if (redirectUrl != null) {
            action = HttpAction.redirect("redirect", context, redirectUrl);
        } else {
            action = HttpAction.ok("ok", context);
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

    /**
     * Execute a logout request.
     *
     * @param context the web context
     * @param request the logout request
     */
    protected void executeLogoutRequest(final C context, final LogoutRequest request) {

    }

    public boolean isKillSession() {
        return killSession;
    }

    public void setKillSession(boolean killSession) {
        this.killSession = killSession;
    }
}
