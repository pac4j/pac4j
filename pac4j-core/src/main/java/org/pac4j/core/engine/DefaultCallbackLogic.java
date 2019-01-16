package org.pac4j.core.engine;

import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;

import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.SeeOtherAction;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;

import java.util.List;
import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>Default callback logic:</p>
 * <p>The credentials are extracted from the current request to fetch the user profile (from the identity provider) which is then saved in
 * the web session. Finally, the user is redirected back to the originally requested url (or to the <code>defaultUrl</code>).</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class DefaultCallbackLogic<R, C extends WebContext> extends AbstractExceptionAwareLogic<R, C> implements CallbackLogic<R, C> {

    private ClientFinder clientFinder = new DefaultCallbackClientFinder();

    @Override
    public R perform(final C context, final Config config, final HttpActionAdapter<R, C> httpActionAdapter,
                     final String inputDefaultUrl, final Boolean inputSaveInSession, final Boolean inputMultiProfile,
                     final Boolean inputRenewSession, final String client) {

        logger.debug("=== CALLBACK ===");

        HttpAction action;
        try {

            // default values
            final String defaultUrl;
            if (inputDefaultUrl == null) {
                defaultUrl = Pac4jConstants.DEFAULT_URL_VALUE;
            } else {
                defaultUrl = inputDefaultUrl;
            }
            final boolean saveInSession;
            if (inputSaveInSession == null) {
                saveInSession = true;
            } else {
                saveInSession = inputSaveInSession;
            }
            final boolean multiProfile;
            if (inputMultiProfile == null) {
                multiProfile = false;
            } else {
                multiProfile = inputMultiProfile;
            }
            final boolean renewSession;
            if (inputRenewSession == null) {
                renewSession = true;
            } else {
                renewSession = inputRenewSession;
            }

            // checks
            assertNotNull("clientFinder", clientFinder);
            assertNotNull("context", context);
            assertNotNull("config", config);
            assertNotNull("httpActionAdapter", httpActionAdapter);
            assertNotBlank(Pac4jConstants.DEFAULT_URL, defaultUrl);
            final Clients clients = config.getClients();
            assertNotNull("clients", clients);

            // logic
            final List<Client> foundClients = clientFinder.find(clients, context, client);
            assertTrue(foundClients != null && foundClients.size() == 1,
                "unable to find one indirect client for the callback: check the callback URL for a client name parameter or suffix path"
                    + " or ensure that your configuration defaults to one indirect client");
            final Client foundClient = foundClients.get(0);
            logger.debug("foundClient: {}", foundClient);
            assertNotNull("foundClient", foundClient);

            final Optional<Credentials> credentials = foundClient.getCredentials(context);
            logger.debug("credentials: {}", credentials);

            if (credentials.isPresent()) {
                final UserProfile profile = foundClient.getUserProfile(credentials.get(), context);
                logger.debug("profile: {}", profile);
                saveUserProfile(context, config, profile, saveInSession, multiProfile, renewSession);
            }

            action = redirectToOriginallyRequestedUrl(context, defaultUrl);

        } catch (final RuntimeException e) {
            return handleException(e, httpActionAdapter, context);
        }

        return httpActionAdapter.adapt(action, context);
    }

    protected void saveUserProfile(final C context, final Config config, final UserProfile profile,
                                   final boolean saveInSession, final boolean multiProfile, final boolean renewSession) {
        final ProfileManager manager = getProfileManager(context);
        if (profile != null) {
            manager.save(saveInSession, profile, multiProfile);
            if (renewSession) {
                renewSession(context, config);
            }
        }
    }

    protected void renewSession(final C context, final Config config) {
        final SessionStore<C> sessionStore = context.getSessionStore();
        if (sessionStore != null) {
            final String oldSessionId = sessionStore.getOrCreateSessionId(context);
            final boolean renewed = sessionStore.renewSession(context);
            if (renewed) {
                final String newSessionId = sessionStore.getOrCreateSessionId(context);
                logger.debug("Renewing session: {} -> {}", oldSessionId, newSessionId);
                final Clients clients = config.getClients();
                if (clients != null) {
                    final List<Client> clientList = clients.getClients();
                    for (final Client client : clientList) {
                        final BaseClient baseClient = (BaseClient) client;
                        baseClient.notifySessionRenewal(oldSessionId, context);
                    }
                }
            } else {
                logger.error("Unable to renew the session. The session store may not support this feature");
            }
        } else {
            logger.error("No session store available for this web context");
        }
    }

    protected HttpAction redirectToOriginallyRequestedUrl(final C context, final String defaultUrl) {
        final String requestedUrl = (String) context.getSessionStore().get(context, Pac4jConstants.REQUESTED_URL);
        String redirectUrl = defaultUrl;
        if (isNotBlank(requestedUrl)) {
            context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, null);
            redirectUrl = requestedUrl;
        }
        logger.debug("redirectUrl: {}", redirectUrl);
        if (ContextHelper.isPost(context)) {
            return new SeeOtherAction(redirectUrl);
        } else {
            return new FoundAction(redirectUrl);
        }
    }

    public ClientFinder getClientFinder() {
        return clientFinder;
    }

    public void setClientFinder(final ClientFinder clientFinder) {
        this.clientFinder = clientFinder;
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "clientFinder", clientFinder, "errorUrl", getErrorUrl());
    }
}
