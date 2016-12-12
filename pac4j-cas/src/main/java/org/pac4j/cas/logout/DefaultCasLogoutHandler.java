package org.pac4j.cas.logout;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jasig.cas.client.session.SingleSignOutHandler;
import org.pac4j.cas.client.CasClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * This class is the logout handler for the {@link CasClient}, inspired by the {@link SingleSignOutHandler} of the Apereo CAS client.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class DefaultCasLogoutHandler<C extends WebContext> implements CasLogoutHandler<C> {

    protected static final Logger logger = LoggerFactory.getLogger(DefaultCasLogoutHandler.class);

    private final Cache<String, Object> cache;

    private boolean killSession;

    public DefaultCasLogoutHandler() {
        this(10000, 30, TimeUnit.MINUTES);
    }

    public DefaultCasLogoutHandler(final long cacheSize, final long timeout, final TimeUnit timeUnit) {
        this.cache = CacheBuilder.newBuilder().maximumSize(cacheSize)
                .expireAfterWrite(timeout, timeUnit).build();
    }

    @Override
    public void recordSession(final C context, final String ticket) {
        final SessionStore sessionStore = context.getSessionStore();
        if (sessionStore == null) {
            logger.error("No session store available for this web context");
        } else {
            final String sessionId = sessionStore.getOrCreateSessionId(context);
            final Object trackableSession = sessionStore.getTrackableSession(context);

            if (trackableSession != null) {
                logger.debug("ticket: {} -> trackableSession: {}", ticket, trackableSession);
                logger.debug("sessionId: {}", sessionId);
                cache.put(ticket, trackableSession);
                cache.put(sessionId, ticket);
            } else {
                logger.debug("No trackable session for the current session store: {}", sessionStore);
            }
        }
    }

    @Override
    public void destroySessionFront(final C context, final String ticket) {
        cache.invalidate(ticket);

        final SessionStore sessionStore = context.getSessionStore();
        if (sessionStore == null) {
            logger.error("No session store available for this web context");
        } else {
            final String currentSessionId = sessionStore.getOrCreateSessionId(context);
            logger.debug("currentSessionId: {}", currentSessionId);
            final String sessionToTicket = (String) cache.getIfPresent(currentSessionId);
            logger.debug("-> ticket: {}", ticket);
            cache.invalidate(currentSessionId);

            if (CommonHelper.areEquals(ticket, sessionToTicket)) {
                destroy(context, sessionStore, "front");
            } else {
                logger.error("The user profiles (and session) can not be destroyed for CAS front channel logout because the provided ticket is not the same as the one linked to the current session");
            }
        }
    }

    protected void destroy(final C context, final SessionStore sessionStore, final String channel) {
        // remove profiles
        final ProfileManager manager = new ProfileManager(context);
        manager.logout();
        logger.debug("destroy the user profiles");
        // and optionally the web session
        if (killSession) {
            logger.debug("destroy the whole session");
            final boolean invalidated = sessionStore.killSession(context);
            if (!invalidated) {
                logger.error("The session has not been invalidated for {} channel logout", channel);
            }
        }
    }

    @Override
    public void destroySessionBack(final C context, final String ticket) {
        final Object trackableSession = cache.getIfPresent(ticket);
        logger.debug("ticket: {} -> trackableSession: {}", ticket, trackableSession);
        if (trackableSession == null) {
            logger.error("No trackable session found for back channel logout. Either the session store does not support to track session or it has expired from the store and the store settings must be updated (expired data)");
        } else {
            cache.invalidate(ticket);

            // renew context with the original session store
            final SessionStore sessionStore = context.getSessionStore();
            if (sessionStore == null) {
                logger.error("No session store available for this web context");
            } else {
                final SessionStore<C> newSessionStore = sessionStore.buildFromTrackableSession(context, trackableSession);
                if (newSessionStore != null) {
                    logger.debug("newSesionStore: {}", newSessionStore);
                    context.setSessionStore(newSessionStore);
                    final String sessionId = newSessionStore.getOrCreateSessionId(context);
                    logger.debug("remove sessionId: {}", sessionId);
                    cache.invalidate(sessionId);

                    destroy(context, newSessionStore, "back");
                } else {
                    logger.error("The session store should be able to build a new session store from the tracked session");
                }
            }
        }
    }

    @Override
    public void renewSession(final String oldSessionId, final C context) {
        final String ticket = (String) cache.getIfPresent(oldSessionId);
        logger.debug("oldSessionId: {} -> ticket: {}", oldSessionId, ticket);
        if (ticket != null) {
            cache.invalidate(ticket);
            cache.invalidate(oldSessionId);
            recordSession(context, ticket);
        }
    }

    public boolean isKillSession() {
        return killSession;
    }

    public void setKillSession(final boolean killSession) {
        this.killSession = killSession;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "cache", cache, "killSession", killSession);
    }
}
