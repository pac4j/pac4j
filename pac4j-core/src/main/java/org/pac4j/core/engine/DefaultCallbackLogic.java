package org.pac4j.core.engine;

import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.ProfileManagerFactoryAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>Default callback logic:</p>
 * <p>The credentials are extracted from the current request to fetch the user profile (from the identity provider) which is then saved in the web session.
 * Finally, the user is redirected back to the originally requested url (or to the <code>defaultUrl</code>).</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class DefaultCallbackLogic<R, C extends WebContext> extends ProfileManagerFactoryAware<C> implements CallbackLogic<R, C> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public R perform(final C context, final Config config, final HttpActionAdapter<R, C> httpActionAdapter,
                     final String inputDefaultUrl, final Boolean inputMultiProfile, final Boolean inputRenewSession) {

        logger.debug("=== CALLBACK ===");

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
        assertNotNull("context", context);
        assertNotNull("config", config);
        assertNotNull("httpActionAdapter", httpActionAdapter);
        assertNotBlank(Pac4jConstants.DEFAULT_URL, defaultUrl);
        final Clients clients = config.getClients();
        assertNotNull("clients", clients);

        // logic
        final Client client = clients.findClient(context);
        logger.debug("client: {}", client);
        assertNotNull("client", client);
        assertTrue(client instanceof IndirectClient, "only indirect clients are allowed on the callback url");

        HttpAction action;
        try {
            final Credentials credentials = client.getCredentials(context);
            logger.debug("credentials: {}", credentials);

            final CommonProfile profile = client.getUserProfile(credentials, context);
            logger.debug("profile: {}", profile);
            saveUserProfile(context, config, profile, multiProfile, renewSession);
            action = redirectToOriginallyRequestedUrl(context, defaultUrl);

        } catch (final HttpAction e) {
            logger.debug("extra HTTP action required in callback: {}", e.getCode());
            action = e;
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
        final String requestedUrl = (String) context.getSessionAttribute(Pac4jConstants.REQUESTED_URL);
        String redirectUrl = defaultUrl;
        if (isNotBlank(requestedUrl)) {
            context.setSessionAttribute(Pac4jConstants.REQUESTED_URL, null);
            redirectUrl = requestedUrl;
        }
        logger.debug("redirectUrl: {}", redirectUrl);
        return HttpAction.redirect("redirect", context, redirectUrl);
    }
}
