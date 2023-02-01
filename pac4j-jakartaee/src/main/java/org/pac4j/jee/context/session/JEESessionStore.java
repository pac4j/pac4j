package org.pac4j.jee.context.session;

import jakarta.servlet.http.HttpSession;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.jee.context.JEEContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Store data in the JEE web session.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
@Slf4j
@ToString
public class JEESessionStore implements SessionStore {

    public static final JEESessionStore INSTANCE = new JEESessionStore();

    protected HttpSession httpSession;

    protected JEESessionStore() {}

    protected JEESessionStore(final HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    protected Optional<HttpSession> getNativeSession(final WebContext context, final boolean createSession) {
        if (httpSession != null) {
            LOGGER.debug("Provided session: {}", httpSession);
            return Optional.of(httpSession);
        } else {
            val jeeContext = (JEEContext) context;
            val session = jeeContext.getNativeRequest().getSession(createSession);
            LOGGER.debug("createSession: {}, retrieved session: {}", createSession, session);
            return Optional.ofNullable(session);
        }
    }

    @Override
    public Optional<String> getSessionId(final WebContext context, final boolean createSession) {
        val httpSession = getNativeSession(context, createSession);
        if (httpSession.isPresent()) {
            val sessionId = httpSession.get().getId();
            LOGGER.debug("Get sessionId: {}", sessionId);
            return Optional.of(sessionId);
        } else {
            LOGGER.debug("No sessionId");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Object> get(final WebContext context, final String key) {
        val httpSession = getNativeSession(context, false);
        if (httpSession.isPresent()) {
            val value = httpSession.get().getAttribute(key);
            LOGGER.debug("Get value: {} for key: {}", value, key);
            return Optional.ofNullable(value);
        } else {
            LOGGER.debug("Can't get value for key: {}, no session available", key);
            return Optional.empty();
        }
    }

    @Override
    public void set(final WebContext context, final String key, final Object value) {
        if (value == null) {
            val httpSession = getNativeSession(context, false);
            if (httpSession.isPresent()) {
                LOGGER.debug("Remove value for key: {}", key);
                httpSession.get().removeAttribute(key);
            }
        } else {
            val httpSession = getNativeSession(context, true);
            if (value instanceof Exception) {
                LOGGER.debug("Set key: {} for value: {}", key, value.toString());
            } else {
                LOGGER.debug("Set key: {} for value: {}", key, value);
            }
            httpSession.get().setAttribute(key, value);
        }
    }

    @Override
    public boolean destroySession(final WebContext context) {
        val httpSession = getNativeSession(context, false);
        if (httpSession.isPresent()) {
            val session = httpSession.get();
            LOGGER.debug("Invalidate session: {}", session);
            session.invalidate();
        }
        return true;
    }

    @Override
    public Optional<Object> getTrackableSession(final WebContext context) {
        val httpSession = getNativeSession(context, false);
        if (httpSession.isPresent()) {
            val session = httpSession.get();
            LOGGER.debug("Return trackable session: {}", session);
            return Optional.of(session);
        } else {
            LOGGER.debug("No trackable session");
            return Optional.empty();
        }
    }

    @Override
    public Optional<SessionStore> buildFromTrackableSession(final WebContext context, final Object trackableSession) {
        if (trackableSession != null) {
            LOGGER.debug("Rebuild session from trackable session: {}", trackableSession);
            return Optional.of(new JEESessionStore((HttpSession) trackableSession));
        } else {
            LOGGER.debug("Unable to build session from trackable session");
            return Optional.empty();
        }
    }

    @Override
    public boolean renewSession(final WebContext context) {
        Map<String, Object> attributes = new HashMap<>();
        val request = ((JEEContext) context).getNativeRequest();
        val session = request.getSession(false);
        if (session != null) {
            LOGGER.debug("Discard old session: {}", session.getId());
            attributes = Collections.list(session.getAttributeNames())
                .stream()
                .collect(Collectors.toMap(k -> k, session::getAttribute, (a, b) -> b));
            session.invalidate();
        }
        val newSession = request.getSession(true);
        LOGGER.debug("And copy all data to the new one: {}", newSession.getId());
        attributes.forEach(newSession::setAttribute);
        return true;
    }
}
