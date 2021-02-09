package org.pac4j.core.engine;

import org.pac4j.core.client.BaseClient;

import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.savedrequest.DefaultSavedRequestHandler;
import org.pac4j.core.engine.savedrequest.SavedRequestHandler;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>Default callback logic:</p>
 * <p>The credentials are extracted from the current request to fetch the user profile (from the identity provider) which is then saved in
 * the web session. Finally, the user is redirected back to the originally requested url (or to the <code>defaultUrl</code>).</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class DefaultCallbackLogic extends AbstractExceptionAwareLogic implements CallbackLogic {

    public static final DefaultCallbackLogic INSTANCE = new DefaultCallbackLogic();

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCallbackLogic.class);

    private ClientFinder clientFinder = new DefaultCallbackClientFinder();

    private SavedRequestHandler savedRequestHandler = new DefaultSavedRequestHandler();

    @Override
    public Object perform(final WebContext webContext, final SessionStore sessionStore, final Config config,
                          final HttpActionAdapter httpActionAdapter, final String inputDefaultUrl, final Boolean inputRenewSession,
                          final String defaultClient) {

        LOGGER.debug("=== CALLBACK ===");

        HttpAction action;
        try {

            // default values
            final String defaultUrl;
            if (inputDefaultUrl == null) {
                defaultUrl = Pac4jConstants.DEFAULT_URL_VALUE;
            } else {
                defaultUrl = inputDefaultUrl;
            }
            final var renewSession = inputRenewSession == null || inputRenewSession;

            // checks
            assertNotNull("clientFinder", clientFinder);
            assertNotNull("webContext", webContext);
            assertNotNull("config", config);
            assertNotNull("httpActionAdapter", httpActionAdapter);
            assertNotBlank(Pac4jConstants.DEFAULT_URL, defaultUrl);
            final var clients = config.getClients();
            assertNotNull("clients", clients);

            // logic
            final var foundClients = clientFinder.find(clients, webContext, defaultClient);
            assertTrue(foundClients != null && foundClients.size() == 1,
                "unable to find one indirect client for the callback: check the callback URL for a client name parameter or suffix path"
                    + " or ensure that your configuration defaults to one indirect client");
            final var foundClient = foundClients.get(0);
            LOGGER.debug("foundClient: {}", foundClient);
            assertNotNull("foundClient", foundClient);

            final var credentials = foundClient.getCredentials(webContext, sessionStore);
            LOGGER.debug("credentials: {}", credentials);

            final var optProfile = foundClient.getUserProfile(credentials.orElse(null), webContext, sessionStore);
            LOGGER.debug("optProfile: {}", optProfile);
            if (optProfile.isPresent()) {
                final var profile = optProfile.get();
                final boolean saveProfileInSession = ((BaseClient) foundClient).getSaveProfileInSession(webContext, profile);
                final var multiProfile = ((BaseClient) foundClient).isMultiProfile(webContext, profile);
                LOGGER.debug("saveProfileInSession: {} / multiProfile: {}", saveProfileInSession, multiProfile);
                saveUserProfile(webContext, sessionStore, config, profile, saveProfileInSession, multiProfile, renewSession);
            }

            action = redirectToOriginallyRequestedUrl(webContext, sessionStore, defaultUrl);

        } catch (final RuntimeException e) {
            return handleException(e, httpActionAdapter, webContext);
        }

        return httpActionAdapter.adapt(action, webContext);
    }

    protected void saveUserProfile(final WebContext context, final SessionStore sessionStore, final Config config,
                                   final UserProfile profile, final boolean saveProfileInSession, final boolean multiProfile,
                                   final boolean renewSession) {
        final var manager = getProfileManager(context, sessionStore);
        if (profile != null) {
            manager.save(saveProfileInSession, profile, multiProfile);
            if (renewSession) {
                renewSession(context, sessionStore, config);
            }
        }
    }

    protected void renewSession(final WebContext context, final SessionStore sessionStore, final Config config) {
        final var oldSessionId = sessionStore.getSessionId(context, true).get();
        final var renewed = sessionStore.renewSession(context);
        if (renewed) {
            final var newSessionId = sessionStore.getSessionId(context, true).get();
            LOGGER.debug("Renewing session: {} -> {}", oldSessionId, newSessionId);
            final var clients = config.getClients();
            if (clients != null) {
                final var clientList = clients.getClients();
                for (final var client : clientList) {
                    final var baseClient = (BaseClient) client;
                    baseClient.notifySessionRenewal(oldSessionId, context, sessionStore);
                }
            }
        } else {
            LOGGER.error("Unable to renew the session. The session store may not support this feature");
        }
    }

    protected HttpAction redirectToOriginallyRequestedUrl(final WebContext context, final SessionStore sessionStore,
                                                          final String defaultUrl) {
        return savedRequestHandler.restore(context, sessionStore, defaultUrl);
    }

    public ClientFinder getClientFinder() {
        return clientFinder;
    }

    public void setClientFinder(final ClientFinder clientFinder) {
        this.clientFinder = clientFinder;
    }

    public SavedRequestHandler getSavedRequestHandler() {
        return savedRequestHandler;
    }

    public void setSavedRequestHandler(final SavedRequestHandler savedRequestHandler) {
        this.savedRequestHandler = savedRequestHandler;
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "clientFinder", clientFinder, "errorUrl", getErrorUrl(),
            "savedRequestHandler", savedRequestHandler);
    }
}
