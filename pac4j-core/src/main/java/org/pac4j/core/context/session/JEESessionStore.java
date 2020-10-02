package org.pac4j.core.context.session;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
public class JEESessionStore implements SessionStore {

    public static final JEESessionStore INSTANCE = new JEESessionStore();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected HttpSession httpSession;

    public JEESessionStore() {}

    public JEESessionStore(final HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    protected Optional<HttpSession> getNativeSession(final WebContext context, final boolean createSession) {
        if (httpSession != null) {
            return Optional.of(httpSession);
        } else {
            final JEEContext jeeContext = (JEEContext) context;
            return Optional.ofNullable(jeeContext.getNativeRequest().getSession(createSession));
        }
    }

    @Override
    public Optional<String> getSessionId(final WebContext context, final boolean createSession) {
        final Optional<HttpSession> httpSession = getNativeSession(context, createSession);
        if (httpSession.isPresent()) {
            return Optional.of(httpSession.get().getId());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional get(final WebContext context, final String key) {
        final Optional<HttpSession> httpSession = getNativeSession(context, false);
        if (httpSession.isPresent()) {
            return Optional.ofNullable(httpSession.get().getAttribute(key));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void set(final WebContext context, final String key, final Object value) {
        if (value == null) {
            final Optional<HttpSession> httpSession = getNativeSession(context, false);
            if (httpSession.isPresent()) {
                httpSession.get().removeAttribute(key);
            }
        } else {
            final Optional<HttpSession> httpSession = getNativeSession(context, true);
            httpSession.get().setAttribute(key, value);
        }
    }

    @Override
    public boolean destroySession(final WebContext context) {
        final Optional<HttpSession> httpSession = getNativeSession(context, false);
        if (httpSession.isPresent()) {
            httpSession.get().invalidate();
        }
        return true;
    }

    @Override
    public Optional getTrackableSession(final WebContext context) {
        final Optional<HttpSession> httpSession = getNativeSession(context, false);
        if (httpSession.isPresent()) {
            return httpSession;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<SessionStore> buildFromTrackableSession(final WebContext context, final Object trackableSession) {
        if (trackableSession != null) {
            return Optional.of(new JEESessionStore((HttpSession) trackableSession));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean renewSession(final WebContext context) {
        Map<String, Object> attributes = new HashMap<>();
        final HttpServletRequest request = ((JEEContext) context).getNativeRequest();
        final HttpSession session = request.getSession(false);
        if (session != null) {
            logger.debug("Discard old session: {}", session.getId());
            attributes = Collections.list(session.getAttributeNames())
                .stream()
                .collect(Collectors.toMap(k -> k, session::getAttribute, (a, b) -> b));
            session.invalidate();
        }
        final HttpSession newSession = request.getSession(true);
        logger.debug("And copy all data to the new one: {}", newSession.getId());
        attributes.forEach((k, v) -> newSession.setAttribute(k, v));
        return true;
    }
}
