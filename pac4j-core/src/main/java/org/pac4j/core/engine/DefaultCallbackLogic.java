package org.pac4j.core.engine;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultCallbackClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.engine.savedrequest.DefaultSavedRequestHandler;
import org.pac4j.core.engine.savedrequest.SavedRequestHandler;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.Pac4jConstants;

import java.util.Objects;

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
@Accessors(chain = true)
public class DefaultCallbackLogic extends AbstractExceptionAwareLogic implements CallbackLogic {

    /** Constant <code>INSTANCE</code> */
    public static final DefaultCallbackLogic INSTANCE = new DefaultCallbackLogic();

    private ClientFinder clientFinder = new DefaultCallbackClientFinder();

    private SavedRequestHandler savedRequestHandler = new DefaultSavedRequestHandler();

    /** {@inheritDoc} */
    @Override
    public Object perform(final Config config, final String inputDefaultUrl, final Boolean inputRenewSession,
                          final String defaultClient, final FrameworkParameters parameters) {

        LOGGER.debug("=== CALLBACK ===");

        // checks
        val ctx = buildContext(config, parameters);
        val webContext = ctx.webContext();
        val httpActionAdapter = config.getHttpActionAdapter();
        assertNotNull("httpActionAdapter", httpActionAdapter);

        HttpAction action;
        try {
            assertNotNull("clientFinder", clientFinder);

            // default values
            final String defaultUrl;
            defaultUrl = Objects.requireNonNullElse(inputDefaultUrl, Pac4jConstants.DEFAULT_URL_VALUE);
            val renewSession = inputRenewSession == null || inputRenewSession;

            assertNotBlank(Pac4jConstants.DEFAULT_URL, defaultUrl);
            val clients = config.getClients();
            assertNotNull("clients", clients);

            val foundClients = clientFinder.find(clients, webContext, defaultClient);
            assertTrue(foundClients != null && foundClients.size() == 1,
                "unable to find one indirect client for the callback: check the callback URL for a client name parameter or suffix path"
                    + " or ensure that your configuration defaults to one indirect client");
            val foundClient = foundClients.get(0);
            LOGGER.debug("foundClient: {}", foundClient);
            assertNotNull("foundClient", foundClient);

            var credentials = foundClient.getCredentials(ctx).orElse(null);
            LOGGER.debug("extracted credentials: {}", credentials);
            credentials = foundClient.validateCredentials(ctx, credentials).orElse(null);
            LOGGER.debug("validated credentials: {}", credentials);

            if (credentials != null && !credentials.isForAuthentication()) {

                action = foundClient.processLogout(ctx, credentials);

            } else {

                if (credentials != null) {
                    val optProfile = foundClient.getUserProfile(ctx, credentials);
                    LOGGER.debug("optProfile: {}", optProfile);
                    if (optProfile.isPresent()) {
                        val profile = optProfile.get();
                        val saveProfileInSession = ((BaseClient) foundClient).getSaveProfileInSession(webContext, profile);
                        val multiProfile = ((BaseClient) foundClient).isMultiProfile(webContext, profile);
                        LOGGER.debug("saveProfileInSession: {} / multiProfile: {}", saveProfileInSession, multiProfile);
                        saveUserProfile(ctx, config, profile, saveProfileInSession, multiProfile, renewSession);
                    }
                }

                action = redirectToOriginallyRequestedUrl(ctx, defaultUrl);
            }

        } catch (final RuntimeException e) {
            return handleException(e, httpActionAdapter, webContext);
        }

        return httpActionAdapter.adapt(action, webContext);
    }

    /**
     * <p>saveUserProfile.</p>
     *
     * @param ctx a {@link CallContext} object
     * @param config a {@link Config} object
     * @param profile a {@link UserProfile} object
     * @param saveProfileInSession a boolean
     * @param multiProfile a boolean
     * @param renewSession a boolean
     */
    protected void saveUserProfile(final CallContext ctx, final Config config, final UserProfile profile,
                                   final boolean saveProfileInSession, final boolean multiProfile, final boolean renewSession) {
        val manager = ctx.profileManagerFactory().apply(ctx.webContext(), ctx.sessionStore());
        if (profile != null) {
            manager.save(saveProfileInSession, profile, multiProfile);
            if (renewSession) {
                renewSession(ctx, config);
            }
        }
    }

    /**
     * <p>renewSession.</p>
     *
     * @param ctx a {@link CallContext} object
     * @param config a {@link Config} object
     */
    protected void renewSession(final CallContext ctx, final Config config) {
        val context = ctx.webContext();
        val sessionStore = ctx.sessionStore();

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
                            baseClient.notifySessionRenewal(ctx, oldSessionId);
                        }
                    }
                }
            } else {
                LOGGER.error("Unable to renew the session. The session store may not support this feature");
            }
        }
    }

    /**
     * <p>redirectToOriginallyRequestedUrl.</p>
     *
     * @param ctx a {@link CallContext} object
     * @param defaultUrl a {@link String} object
     * @return a {@link HttpAction} object
     */
    protected HttpAction redirectToOriginallyRequestedUrl(final CallContext ctx, final String defaultUrl) {
        return savedRequestHandler.restore(ctx, defaultUrl);
    }
}
