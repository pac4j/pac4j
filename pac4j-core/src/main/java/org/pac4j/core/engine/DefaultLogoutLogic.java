package org.pac4j.core.engine;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.NoContentAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.List;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>Default logout logic:</p>
 *
 * <p>If the <code>localLogout</code> property is <code>true</code>, the pac4j profiles are removed from the web session
 * (and the web session is destroyed if the <code>destroySession</code> property is <code>true</code>).</p>
 *
 * <p>A post logout action is computed as the redirection to the <code>url</code> request parameter if it matches the
 * <code>logoutUrlPattern</code> or to the <code>defaultUrl</code> if it is defined or as a blank page otherwise.</p>
 *
 * <p>If the <code>centralLogout</code> property is <code>true</code>, the user is redirected to the identity provider
 * for a central logout and then optionally to the post logout redirection URL (if it's supported by the identity provider and if it's an
 * absolute URL). If no central logout is defined, the post logout action is performed directly.</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class DefaultLogoutLogic<R, C extends WebContext> extends AbstractExceptionAwareLogic<R, C> implements LogoutLogic<R, C> {

    @Override
    public R perform(final C context, final Config config, final HttpActionAdapter<R, C> httpActionAdapter,
                     final String defaultUrl, final String inputLogoutUrlPattern, final Boolean inputLocalLogout,
                     final Boolean inputDestroySession, final Boolean inputCentralLogout) {

        logger.debug("=== LOGOUT ===");

        HttpAction action;
        try {

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
            final boolean destroySession;
            if (inputDestroySession == null) {
                destroySession = false;
            } else {
                destroySession = inputDestroySession;
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
            if (redirectUrl != null) {
                action = new FoundAction(redirectUrl);
            } else {
                action = NoContentAction.INSTANCE;
            }

            // local logout if requested or multiple profiles
            if (localLogout || profiles.size() > 1) {
                logger.debug("Performing application logout");
                manager.logout();
                if (destroySession) {
                    final SessionStore sessionStore = context.getSessionStore();
                    if (sessionStore != null) {
                        final boolean removed = sessionStore.destroySession(context);
                        if (!removed) {
                            logger.error("Unable to destroy the web session. The session store may not support this feature");
                        }
                    } else {
                        logger.error("No session store available for this web context");
                    }
                }
            }

            // central logout
            if (centralLogout) {
                logger.debug("Performing central logout");
                for (final CommonProfile profile : profiles) {
                    logger.debug("Profile: {}", profile);
                    final String clientName = profile.getClientName();
                    if (clientName != null) {
                        final Client client = configClients.findClient(clientName);
                        if (client != null) {
                            final String targetUrl;
                            if (redirectUrl != null && (redirectUrl.startsWith(HttpConstants.SCHEME_HTTP) ||
                                redirectUrl.startsWith(HttpConstants.SCHEME_HTTPS))) {
                                targetUrl = redirectUrl;
                            } else {
                                targetUrl = null;
                            }
                            final Optional<RedirectionAction> logoutAction = client.getLogoutAction(context, profile, targetUrl);
                            logger.debug("Logout action: {}", logoutAction);
                            if (logoutAction.isPresent()) {
                                action = logoutAction.get();
                                break;
                            }
                        }
                    }
                }
            }

        } catch (final RuntimeException e) {
            return handleException(e, httpActionAdapter, context);
        }

        return httpActionAdapter.adapt(action, context);
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "errorUrl", getErrorUrl());
    }
}
