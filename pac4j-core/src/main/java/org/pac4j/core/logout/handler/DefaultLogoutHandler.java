package org.pac4j.core.logout.handler;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.store.GuavaStore;
import org.pac4j.core.store.Store;
import org.pac4j.core.util.CommonHelper;

import java.util.concurrent.TimeUnit;

/**
 * Default logout handler.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@ToString
@Getter
@Setter
@Slf4j
public class DefaultLogoutHandler implements LogoutHandler {

    private Store<String, Object> store = new GuavaStore<>(10000, 30, TimeUnit.MINUTES);

    private boolean destroySession;

    private ProfileManagerFactory profileManagerFactory;

    public DefaultLogoutHandler() {}

    public DefaultLogoutHandler(final Store<String, Object> store, final ProfileManagerFactory profileManagerFactory) {
        this.store = store;
        this.profileManagerFactory = profileManagerFactory;
    }

    @Override
    public void recordSession(final WebContext context, final SessionStore sessionStore, final String key) {
        if (sessionStore == null) {
            LOGGER.error("No session store available for this web context");
        } else {
            val optSessionId = sessionStore.getSessionId(context, true);
            if (optSessionId.isEmpty()) {
                LOGGER.error("No session identifier retrieved although the session creation has been requested");
            } else {
                val sessionId = optSessionId.get();
                val optTrackableSession = sessionStore.getTrackableSession(context);

                if (optTrackableSession.isPresent()) {
                    val trackableSession = optTrackableSession.get();
                    LOGGER.debug("key: {} -> trackableSession: {}", key, trackableSession);
                    LOGGER.debug("sessionId: {}", sessionId);
                    store.set(key, trackableSession);
                    store.set(sessionId, key);
                } else {
                    LOGGER.debug("No trackable session for the current session store: {}", sessionStore);
                }
            }
        }
    }

    @Override
    public void destroySessionFront(final WebContext context, final SessionStore sessionStore, final String key) {
        if (sessionStore == null) {
            LOGGER.error("No session store available for this web context");
        } else {
            val optCurrentSessionId = sessionStore.getSessionId(context, false);

            if (optCurrentSessionId.isPresent()) {
                store.remove(key);
                val currentSessionId = optCurrentSessionId.get();
                LOGGER.debug("currentSessionId: {}", currentSessionId);
                val sessionToKey = (String) store.get(currentSessionId).orElse(null);
                LOGGER.debug("-> key: {}", key);
                store.remove(currentSessionId);

                if (CommonHelper.areEquals(key, sessionToKey)) {
                    destroy(context, sessionStore, "front");
                } else {
                    LOGGER.error("The user profiles (and session) can not be destroyed for the front channel logout because the provided "
                        + "key is not the same as the one linked to the current session");
                }
            } else {
                LOGGER.warn("no session for front channel logout => trying back channel logout");
                destroySessionBack(context, sessionStore, key);
            }
        }
    }

    protected void destroy(final WebContext context, final SessionStore sessionStore, final String channel) {
        // remove profiles
        val manager = profileManagerFactory.apply(context, sessionStore);
        manager.removeProfiles();
        LOGGER.debug("{} channel logout call: destroy the user profiles", channel);
        // and optionally the web session
        if (destroySession) {
            LOGGER.debug("destroy the whole session");
            final var invalidated = sessionStore.destroySession(context);
            if (!invalidated) {
                LOGGER.error("The session has not been invalidated");
            }
        }
    }

    @Override
    public void destroySessionBack(final WebContext context, final SessionStore sessionStore, final String key) {
        val optTrackableSession = store.get(key);
        LOGGER.debug("key: {} -> trackableSession: {}", key, optTrackableSession);
        if (!optTrackableSession.isPresent()) {
            LOGGER.error("No trackable session found for back channel logout. Either the session store does not support to track session "
                + "or it has expired from the store and the store settings must be updated (expired data)");
        } else {
            store.remove(key);

            // renew context with the original session store
            if (sessionStore == null) {
                LOGGER.error("No session store available for this web context");
            } else {
                val optNewSessionStore = sessionStore
                    .buildFromTrackableSession(context, optTrackableSession.get());
                if (optNewSessionStore.isPresent()) {
                    val newSessionStore = optNewSessionStore.get();
                    LOGGER.debug("newSesionStore: {}", newSessionStore);
                    val sessionId = newSessionStore.getSessionId(context, true).get();
                    LOGGER.debug("remove sessionId: {}", sessionId);
                    store.remove(sessionId);

                    destroy(context, newSessionStore, "back");
                } else {
                    LOGGER.error("The session store should be able to build a new session store from the tracked session");
                }
            }
        }
    }

    @Override
    public void renewSession(final String oldSessionId, final WebContext context, final SessionStore sessionStore) {
        val optKey = store.get(oldSessionId);
        LOGGER.debug("oldSessionId: {} -> key: {}", oldSessionId, optKey);
        if (optKey.isPresent()) {
            val key = (String) optKey.get();
            store.remove(key);
            store.remove(oldSessionId);
            recordSession(context, sessionStore, key);
        }
    }
}
