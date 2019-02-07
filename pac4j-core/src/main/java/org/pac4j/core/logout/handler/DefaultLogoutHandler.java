package org.pac4j.core.logout.handler;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.factory.ProfileManagerFactory2Aware;
import org.pac4j.core.store.GuavaStore;
import org.pac4j.core.store.Store;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Default logout handler.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class DefaultLogoutHandler<C extends WebContext> extends ProfileManagerFactory2Aware<C> implements LogoutHandler<C> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Store<String, Object> store = new GuavaStore<>(10000, 30, TimeUnit.MINUTES);

    private boolean destroySession;

    public DefaultLogoutHandler() {}

    public DefaultLogoutHandler(final Store<String, Object> store) {
        this.store = store;
    }

    @Override
    public void recordSession(final C context, final String key) {
        final SessionStore sessionStore = context.getSessionStore();
        if (sessionStore == null) {
            logger.error("No session store available for this web context");
        } else {
            final String sessionId = sessionStore.getOrCreateSessionId(context);
            final Object trackableSession = sessionStore.getTrackableSession(context);

            if (trackableSession != null) {
                logger.debug("key: {} -> trackableSession: {}", key, trackableSession);
                logger.debug("sessionId: {}", sessionId);
                store.set(key, trackableSession);
                store.set(sessionId, key);
            } else {
                logger.debug("No trackable session for the current session store: {}", sessionStore);
            }
        }
    }

    @Override
    public void destroySessionFront(final C context, final String key) {
        store.remove(key);

        final SessionStore sessionStore = context.getSessionStore();
        if (sessionStore == null) {
            logger.error("No session store available for this web context");
        } else {
            final String currentSessionId = sessionStore.getOrCreateSessionId(context);
            logger.debug("currentSessionId: {}", currentSessionId);
            final String sessionToKey = (String) store.get(currentSessionId).orElse(null);
            logger.debug("-> key: {}", key);
            store.remove(currentSessionId);

            if (CommonHelper.areEquals(key, sessionToKey)) {
                destroy(context, sessionStore, "front");
            } else {
                logger.error("The user profiles (and session) can not be destroyed for the front channel logout because the provided "
                    + "key is not the same as the one linked to the current session");
            }
        }
    }

    protected void destroy(final C context, final SessionStore sessionStore, final String channel) {
        // remove profiles
        final ProfileManager manager = getProfileManager(context, sessionStore);
        manager.logout();
        logger.debug("destroy the user profiles");
        // and optionally the web session
        if (destroySession) {
            logger.debug("destroy the whole session");
            final boolean invalidated = sessionStore.destroySession(context);
            if (!invalidated) {
                logger.error("The session has not been invalidated for {} channel logout", channel);
            }
        }
    }

    @Override
    public void destroySessionBack(final C context, final String key) {
        final Optional trackableSession = store.get(key);
        logger.debug("key: {} -> trackableSession: {}", key, trackableSession);
        if (!trackableSession.isPresent()) {
            logger.error("No trackable session found for back channel logout. Either the session store does not support to track session "
                + "or it has expired from the store and the store settings must be updated (expired data)");
        } else {
            store.remove(key);

            // renew context with the original session store
            final SessionStore sessionStore = context.getSessionStore();
            if (sessionStore == null) {
                logger.error("No session store available for this web context");
            } else {
                final SessionStore<C> newSessionStore = sessionStore.buildFromTrackableSession(context, trackableSession.get());
                if (newSessionStore != null) {
                    logger.debug("newSesionStore: {}", newSessionStore);
                    final String sessionId = newSessionStore.getOrCreateSessionId(context);
                    logger.debug("remove sessionId: {}", sessionId);
                    store.remove(sessionId);

                    destroy(context, newSessionStore, "back");
                } else {
                    logger.error("The session store should be able to build a new session store from the tracked session");
                }
            }
        }
    }

    @Override
    public void renewSession(final String oldSessionId, final C context) {
        final Optional optKey = store.get(oldSessionId);
        logger.debug("oldSessionId: {} -> key: {}", oldSessionId, optKey);
        if (optKey.isPresent()) {
            final String key = (String) optKey.get();
            store.remove(key);
            store.remove(oldSessionId);
            recordSession(context, key);
        }
    }

    public Store<String, Object> getStore() {
        return store;
    }

    public void setStore(final Store<String, Object> store) {
        this.store = store;
    }

    public boolean isDestroySession() {
        return destroySession;
    }

    public void setDestroySession(final boolean destroySession) {
        this.destroySession = destroySession;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "store", store, "destroySession", destroySession);
    }
}
