package org.pac4j.core.engine;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.savedrequest.DefaultSavedRequestHandler;
import org.pac4j.core.engine.savedrequest.SavedRequestHandler;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.util.Pac4jConstants;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>Default callback logic:</p>
 * <p>The credentials are extracted from the current request to fetch the user profile (from the identity provider) which is then saved in
 * the web session. Finally, the user is redirected back to the originally requested url (or to the <code>defaultUrl</code>).</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
@Getter
@Setter
@ToString(callSuper = true)
@Slf4j
public class DefaultCallbackLogic extends AbstractExceptionAwareLogic implements CallbackLogic {

    public static final DefaultCallbackLogic INSTANCE = new DefaultCallbackLogic();

    private ClientFinder clientFinder = new DefaultCallbackClientFinder();

    private SavedRequestHandler savedRequestHandler = new DefaultSavedRequestHandler();

    @Override
    public Object perform(final WebContext webContext, final SessionStore sessionStore, final ProfileManagerFactory profileManagerFactory,
                          final Config config, final HttpActionAdapter httpActionAdapter, final String inputDefaultUrl,
                          final Boolean inputRenewSession, final String defaultClient) {

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
            val renewSession = inputRenewSession == null || inputRenewSession;

            // checks
            assertNotNull("clientFinder", clientFinder);
            assertNotNull("webContext", webContext);
            assertNotNull("config", config);
            assertNotNull("httpActionAdapter", httpActionAdapter);
            assertNotBlank(Pac4jConstants.DEFAULT_URL, defaultUrl);
            val clients = config.getClients();
            assertNotNull("clients", clients);

            // logic
            val foundClients = clientFinder.find(clients, webContext, defaultClient);
            assertTrue(foundClients != null && foundClients.size() == 1,
                "unable to find one indirect client for the callback: check the callback URL for a client name parameter or suffix path"
                    + " or ensure that your configuration defaults to one indirect client");
            val foundClient = foundClients.get(0);
            LOGGER.debug("foundClient: {}", foundClient);
            assertNotNull("foundClient", foundClient);

            val credentials = foundClient.getCredentials(webContext, sessionStore, profileManagerFactory);
            LOGGER.debug("credentials: {}", credentials);

            val optProfile = foundClient.getUserProfile(credentials.orElse(null), webContext, sessionStore);
            LOGGER.debug("optProfile: {}", optProfile);
            if (optProfile.isPresent()) {
                val profile = optProfile.get();
                val saveProfileInSession = ((BaseClient) foundClient).getSaveProfileInSession(webContext, profile);
                val multiProfile = ((BaseClient) foundClient).isMultiProfile(webContext, profile);
                LOGGER.debug("saveProfileInSession: {} / multiProfile: {}", saveProfileInSession, multiProfile);
                saveUserProfile(webContext, sessionStore, profileManagerFactory, config,
                                profile, saveProfileInSession, multiProfile, renewSession);
            }

            action = redirectToOriginallyRequestedUrl(webContext, sessionStore, defaultUrl);

        } catch (final RuntimeException e) {
            return handleException(e, httpActionAdapter, webContext);
        }

        return httpActionAdapter.adapt(action, webContext);
    }

    protected void saveUserProfile(final WebContext context, final SessionStore sessionStore,
                                   final ProfileManagerFactory profileManagerFactory, final Config config, final UserProfile profile,
                                   final boolean saveProfileInSession, final boolean multiProfile, final boolean renewSession) {
        val manager = profileManagerFactory.apply(context, sessionStore);
        if (profile != null) {
            manager.save(saveProfileInSession, profile, multiProfile);
            if (renewSession) {
                renewSession(context, sessionStore, config);
            }
        }
    }

    protected void renewSession(final WebContext context, final SessionStore sessionStore, final Config config) {
        val optOldSessionId = sessionStore.getSessionId(context, true);
        if (optOldSessionId.isEmpty()) {
            LOGGER.error("No old session identifier retrieved although the session creation has been requested");
        } else {
            val oldSessionId = optOldSessionId.get();
            val renewed = sessionStore.renewSession(context);
            if (renewed) {
                val optNewSessionId = sessionStore.getSessionId(context, true);
                if (optNewSessionId.isEmpty()) {
                    LOGGER.error("No new session identifier retrieved although the session creation has been requested");
                } else {
                    val newSessionId = optNewSessionId.get();
                    LOGGER.debug("Renewing session: {} -> {}", oldSessionId, newSessionId);
                    val clients = config.getClients();
                    if (clients != null) {
                        val clientList = clients.getClients();
                        for (val client : clientList) {
                            val baseClient = (BaseClient) client;
                            baseClient.notifySessionRenewal(oldSessionId, context, sessionStore);
                        }
                    }
                }
            } else {
                LOGGER.error("Unable to renew the session. The session store may not support this feature");
            }
        }
    }

    protected HttpAction redirectToOriginallyRequestedUrl(final WebContext context, final SessionStore sessionStore,
                                                          final String defaultUrl) {
        return savedRequestHandler.restore(context, sessionStore, defaultUrl);
    }
}
