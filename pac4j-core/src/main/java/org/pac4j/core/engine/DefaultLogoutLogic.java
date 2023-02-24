package org.pac4j.core.engine;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.NoContentAction;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.regex.Pattern;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * Default logout logic.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
@ToString(callSuper = true)
@Slf4j
public class DefaultLogoutLogic extends AbstractExceptionAwareLogic implements LogoutLogic {

    /** Constant <code>INSTANCE</code> */
    public static final DefaultLogoutLogic INSTANCE = new DefaultLogoutLogic();

    /** {@inheritDoc} */
    @Override
    public Object perform(final Config config, final String defaultUrl, final String inputLogoutUrlPattern, final Boolean inputLocalLogout,
                          final Boolean inputDestroySession, final Boolean inputCentralLogout, final FrameworkParameters parameters) {

        LOGGER.debug("=== LOGOUT ===");

        // checks
        val ctx = buildContext(config, parameters);
        val webContext = ctx.webContext();
        val httpActionAdapter = config.getHttpActionAdapter();
        assertNotNull("httpActionAdapter", httpActionAdapter);

        HttpAction action;
        try {
            val sessionStore = ctx.sessionStore();

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
            val manager = ctx.profileManagerFactory().apply(webContext, sessionStore);
            manager.setConfig(config);
            val profiles = manager.getProfiles();

            // compute redirection URL
            val url = webContext.getRequestParameter(Pac4jConstants.URL);
            var redirectUrl = defaultUrl;
            if (url.isPresent() && Pattern.matches(logoutUrlPattern, url.get())) {
                redirectUrl = url.get();
            }
            LOGGER.debug("redirectUrl: {}", redirectUrl);
            if (redirectUrl != null) {
                action = HttpActionHelper.buildRedirectUrlAction(webContext, redirectUrl);
            } else {
                action = NoContentAction.INSTANCE;
            }

            // local logout if requested or multiple profiles
            if (localLogout || profiles.size() > 1) {
                LOGGER.debug("Performing application logout");
                manager.removeProfiles();
                if (destroySession) {
                    if (sessionStore != null) {
                        val removed = sessionStore.destroySession(webContext);
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
                                redirectUrl = enhanceRedirectUrl(ctx, config, client.get(), redirectUrl);
                                if (redirectUrl.startsWith(HttpConstants.SCHEME_HTTP) ||
                                    redirectUrl.startsWith(HttpConstants.SCHEME_HTTPS)) {
                                    targetUrl = redirectUrl;
                                }
                            }
                            val logoutAction =
                                client.get().getLogoutAction(ctx, profile, targetUrl);
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
            return handleException(e, httpActionAdapter, webContext);
        }

        return httpActionAdapter.adapt(action, webContext);
    }

    /**
     * <p>enhanceRedirectUrl.</p>
     *
     * @param ctx a {@link org.pac4j.core.context.CallContext} object
     * @param config a {@link org.pac4j.core.config.Config} object
     * @param client a {@link org.pac4j.core.client.Client} object
     * @param redirectUrl a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    protected String enhanceRedirectUrl(final CallContext ctx, final Config config, final Client client, final String redirectUrl) {
        return redirectUrl;
    }
}
