package org.pac4j.core.context.session;

import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
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

    protected HttpSession getNativeSession(final WebContext context) {
        return ((JEEContext) context).getNativeRequest().getSession();
    }

    @Override
    public String getOrCreateSessionId(final WebContext context) {
        return getNativeSession(context).getId();
    }

    @Override
    public Optional get(final WebContext context, final String key) {
        return Optional.ofNullable(getNativeSession(context).getAttribute(key));
    }

    @Override
    public void set(final WebContext context, final String key, final Object value) {
        if (value == null) {
            getNativeSession(context).removeAttribute(key);
        } else {
            getNativeSession(context).setAttribute(key, value);
        }
    }

    @Override
    public boolean destroySession(final WebContext context) {
        getNativeSession(context).invalidate();
        return true;
    }

    @Override
    public Optional getTrackableSession(final WebContext context) {
        return Optional.ofNullable(getNativeSession(context));
    }

    @Override
    public Optional<SessionStore> buildFromTrackableSession(final WebContext context, final Object trackableSession) {
        if (trackableSession != null) {
            return Optional.of(new JEEProvidedSessionStore((HttpSession) trackableSession));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean renewSession(final WebContext context) {
        final HttpServletRequest request = ((JEEContext) context).getNativeRequest();
        final HttpSession session = request.getSession();
        logger.debug("Discard old session: {}", session.getId());
        final Map<String, Object> attributes = Collections.list(session.getAttributeNames())
            .stream()
            .collect(Collectors.toMap(k -> k, session::getAttribute, (a, b) -> b));
        session.invalidate();
        final HttpSession newSession = request.getSession(true);
        logger.debug("And copy all data to the new one: {}", newSession.getId());
        attributes.forEach((k, v) -> newSession.setAttribute(k, v));
        return true;
    }
}
