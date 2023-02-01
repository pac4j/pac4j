package org.pac4j.core.logout.handler;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.store.GuavaStore;
import org.pac4j.core.store.Store;
import org.pac4j.core.util.CommonHelper;

import java.util.concurrent.TimeUnit;

/**
 * Default session logout handler.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@ToString
@Getter
@Setter
@Slf4j
public class DefaultSessionLogoutHandler implements SessionLogoutHandler {

    private Store<String, Object> store = new GuavaStore<>(10000, 30, TimeUnit.MINUTES);

    private boolean destroySession;

    public DefaultSessionLogoutHandler() {}

    public DefaultSessionLogoutHandler(final Store<String, Object> store) {
        this.store = store;
    }

    @Override
    public void recordSession(final CallContext ctx, final String key) {
        val webContext = ctx.webContext();
        val sessionStore = ctx.sessionStore();

        if (sessionStore == null) {
            LOGGER.error("No session store available for this web context");
        } else {
            val optSessionId = sessionStore.getSessionId(webContext, true);
            if (optSessionId.isEmpty()) {
                LOGGER.error("No session identifier retrieved although the session creation has been requested");
            } else {
                val sessionId = optSessionId.get();
                val optTrackableSession = sessionStore.getTrackableSession(webContext);

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
    public void destroySession(final CallContext ctx, final String key) {
        val webContext = ctx.webContext();
        val sessionStore = ctx.sessionStore();

        val optTrackableSession = store.get(key);
        if (optTrackableSession.isPresent()) {
            store.remove(key);
        }

        if (sessionStore == null) {
            LOGGER.warn("No session store. Cannot destroy session");
            return;
        }

        val optCurrentSessionId = sessionStore.getSessionId(ctx.webContext(), false);
        if (optCurrentSessionId.isPresent()) {

            val currentSessionId = optCurrentSessionId.get();
            LOGGER.debug("current sessionId: {}", currentSessionId);
            val keyForCurrentSession = (String) store.get(currentSessionId).orElse(null);
            LOGGER.debug("key associated to the current session: {}", key);
            store.remove(currentSessionId);

            if (CommonHelper.areEquals(key, keyForCurrentSession)) {
                destroy(webContext, sessionStore, ctx.profileManagerFactory(), "front");
                return;
            } else {
                LOGGER.debug("Unknown (new) web session: cannot perform front channel logout");
            }
        } else {
            LOGGER.debug("No web session: cannot perform front channel logout");
        }

        LOGGER.debug("TrackableSession: {} for key: {}", optTrackableSession, key);
        if (!optTrackableSession.isPresent()) {
            LOGGER.warn("No trackable session: cannot perform back channel logout");
        } else {

            val optNewSessionStore = sessionStore
                .buildFromTrackableSession(webContext, optTrackableSession.get());
            if (optNewSessionStore.isPresent()) {
                val newSessionStore = optNewSessionStore.get();
                LOGGER.debug("newSesionStore: {}", newSessionStore);
                val sessionId = newSessionStore.getSessionId(webContext, true).get();
                LOGGER.debug("new sessionId: {}", sessionId);
                store.remove(sessionId);

                destroy(webContext, newSessionStore, ctx.profileManagerFactory(), "back");
                return;
            } else {
                LOGGER.warn("Cannot build new session store from tracked session: cannot perform back channel logout");
            }
        }
    }

    protected void destroy(final WebContext webContext, final SessionStore sessionStore,
                           final ProfileManagerFactory profileManagerFactory, final String channel) {
        // remove profiles
        val manager = profileManagerFactory.apply(webContext, sessionStore);
        manager.removeProfiles();
        LOGGER.debug("{} channel logout call: destroy the user profiles", channel);
        // and optionally the web session
        if (destroySession) {
            LOGGER.debug("destroy the whole session");
            val invalidated = sessionStore.destroySession(webContext);
            if (!invalidated) {
                LOGGER.error("The session has not been invalidated");
            }
        }
    }

    @Override
    public void renewSession(final CallContext ctx, final String oldSessionId) {
        val optKey = store.get(oldSessionId);
        LOGGER.debug("oldSessionId: {} -> key: {}", oldSessionId, optKey);
        if (optKey.isPresent()) {
            val key = (String) optKey.get();
            store.remove(key);
            store.remove(oldSessionId);
            recordSession(ctx, key);
        }
    }
}
