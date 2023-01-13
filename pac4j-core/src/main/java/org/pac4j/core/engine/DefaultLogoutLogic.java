package org.pac4j.core.engine;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.NoContentAction;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.regex.Pattern;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;

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
@ToString(callSuper = true)
@Slf4j
public class DefaultLogoutLogic extends AbstractExceptionAwareLogic implements LogoutLogic {

    public static final DefaultLogoutLogic INSTANCE = new DefaultLogoutLogic();

    @Override
    public Object perform(final Config config, final String defaultUrl, final String inputLogoutUrlPattern, final Boolean inputLocalLogout,
                          final Boolean inputDestroySession, final Boolean inputCentralLogout, final FrameworkParameters parameters) {

        LOGGER.debug("=== LOGOUT ===");

        // checks
        assertNotNull("config", config);
        assertNotNull("config.getWebContextFactory()", config.getWebContextFactory());
        val context = config.getWebContextFactory().newContext(parameters);
        assertNotNull("context", context);
        val httpActionAdapter = config.getHttpActionAdapter();
        assertNotNull("httpActionAdapter", httpActionAdapter);

        HttpAction action;
        try {
            assertNotNull("config.getSessionStoreFactory()", config.getSessionStoreFactory());
            val sessionStore = config.getSessionStoreFactory().newSessionStore(parameters);
            assertNotNull("sessionStore", sessionStore);
            val profileManagerFactory = config.getProfileManagerFactory();
            assertNotNull("profileManagerFactory", profileManagerFactory);

            // default values
            final String logoutUrlPattern;
            if (inputLogoutUrlPattern == null) {
                logoutUrlPattern = Pac4jConstants.DEFAULT_LOGOUT_URL_PATTERN_VALUE;
            } else {
                logoutUrlPattern = inputLogoutUrlPattern;
            }
            val localLogout = inputLocalLogout == null || inputLocalLogout;
            val destroySession = inputDestroySession != null && inputDestroySession;
            val centralLogout = inputCentralLogout != null && inputCentralLogout;

            assertNotBlank(Pac4jConstants.LOGOUT_URL_PATTERN, logoutUrlPattern);
            val configClients = config.getClients();
            assertNotNull("configClients", configClients);

            // logic
            val manager = profileManagerFactory.apply(context, sessionStore);
            manager.setConfig(config);
            val profiles = manager.getProfiles();

            // compute redirection URL
            val url = context.getRequestParameter(Pac4jConstants.URL);
            var redirectUrl = defaultUrl;
            if (url.isPresent() && Pattern.matches(logoutUrlPattern, url.get())) {
                redirectUrl = url.get();
            }
            LOGGER.debug("redirectUrl: {}", redirectUrl);
            if (redirectUrl != null) {
                action = HttpActionHelper.buildRedirectUrlAction(context, redirectUrl);
            } else {
                action = NoContentAction.INSTANCE;
            }

            // local logout if requested or multiple profiles
            if (localLogout || profiles.size() > 1) {
                LOGGER.debug("Performing application logout");
                manager.removeProfiles();
                if (destroySession) {
                    if (sessionStore != null) {
                        val removed = sessionStore.destroySession(context);
                        if (!removed) {
                            LOGGER.error("Unable to destroy the web session. The session store may not support this feature");
                        }
                    } else {
                        LOGGER.error("No session store available for this web context");
                    }
                }
            }

            // central logout
            if (centralLogout) {
                LOGGER.debug("Performing central logout");
                for (val profile : profiles) {
                    LOGGER.debug("Profile: {}", profile);
                    val clientName = profile.getClientName();
                    if (clientName != null) {
                        val client = configClients.findClient(clientName);
                        if (client.isPresent()) {
                            String targetUrl = null;
                            if (redirectUrl != null) {
                                redirectUrl = enhanceRedirectUrl(config, client.get(), context, sessionStore, redirectUrl);
                                if (redirectUrl.startsWith(HttpConstants.SCHEME_HTTP) ||
                                    redirectUrl.startsWith(HttpConstants.SCHEME_HTTPS)) {
                                    targetUrl = redirectUrl;
                                }
                            }
                            val logoutAction =
                                client.get().getLogoutAction(context, sessionStore, profileManagerFactory, profile, targetUrl);
                            LOGGER.debug("Logout action: {}", logoutAction);
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

    protected String enhanceRedirectUrl(final Config config, final Client client, final WebContext context,
                                        final SessionStore sessionStore, final String redirectUrl) {
        return redirectUrl;
    }
}
