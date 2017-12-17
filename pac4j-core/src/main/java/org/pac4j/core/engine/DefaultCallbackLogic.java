package org.pac4j.core.engine;

import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

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
                     final String inputDefaultUrl, final Boolean inputMultiProfile, final Boolean inputRenewSession) {

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
            final List<Client> foundClients = clientFinder.find(clients, context, null);
            assertTrue(foundClients != null && foundClients.size() == 1,
                "one and only one indirect client must be retrieved from the callback");
            final Client client = foundClients.get(0);
            logger.debug("client: {}", client);
            assertNotNull("client", client);

            final Optional<Credentials> credentials = client.getCredentials(context);
            logger.debug("credentials: {}", credentials);

            final Optional<CommonProfile> profile = credentials.flatMap(c -> client.getUserProfile(c, context));
            logger.debug("profile: {}", profile);
            saveUserProfile(context, config, profile, multiProfile, renewSession);
            action = redirectToOriginallyRequestedUrl(context, defaultUrl);

        } catch (final RuntimeException e) {
            return handleException(e, httpActionAdapter, context);
        }

        return httpActionAdapter.adapt(action.getCode(), context);
    }

    protected void saveUserProfile(final C context, final Config config, final CommonProfile profile,
                                   final boolean multiProfile, final boolean renewSession) {
        final ProfileManager manager = getProfileManager(context, config);
        if (profile != null) {
            manager.save(true, profile, multiProfile);
            if (renewSession) {
                renewSession(context, config);
            }
        }
    }

    protected void saveUserProfile(final C context, final Config config, final Optional<CommonProfile> profile,
                                   final boolean multiProfile, final boolean renewSession) {
        if (profile.isPresent()) {
            this.saveUserProfile(context, config, profile.get(), multiProfile, renewSession);
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
        final Optional<String> requestedUrlOpt = context.getSessionStore().get(context, Pac4jConstants.REQUESTED_URL);
        String redirectUrl = requestedUrlOpt.filter(url -> isNotBlank(url)).orElse(defaultUrl);
        if (!defaultUrl.equals(redirectUrl)) {
            context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, null);
        }

        logger.debug("redirectUrl: {}", redirectUrl);
        return HttpAction.redirect(context, redirectUrl);
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
