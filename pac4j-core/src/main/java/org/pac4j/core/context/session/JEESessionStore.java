package org.pac4j.core.context.session;

import org.pac4j.core.context.JEEContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Store data in the JEE web session.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class JEESessionStore implements SessionStore<JEEContext> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected HttpSession getHttpSession(final JEEContext context) {
        return context.getNativeRequest().getSession();
    }

    @Override
    public String getOrCreateSessionId(final JEEContext context) {
        return getHttpSession(context).getId();
    }

    @Override
    public Object get(final JEEContext context, final String key) {
        return getHttpSession(context).getAttribute(key);
    }

    @Override
    public void set(final JEEContext context, final String key, final Object value) {
        if (value == null) {
            getHttpSession(context).removeAttribute(key);
        } else {
            getHttpSession(context).setAttribute(key, value);
        }
    }

    @Override
    public boolean destroySession(final JEEContext context) {
        getHttpSession(context).invalidate();
        return true;
    }

    @Override
    public Object getTrackableSession(final JEEContext context) {
        return getHttpSession(context);
    }

    @Override
    public SessionStore<JEEContext> buildFromTrackableSession(final JEEContext context, final Object trackableSession) {
        if (trackableSession != null) {
            return new JEEProvidedSessionStore((HttpSession) trackableSession);
        } else {
            return null;
        }
    }

    @Override
    public boolean renewSession(final JEEContext context) {
        final HttpServletRequest request = context.getNativeRequest();
        final HttpSession session = request.getSession();
        logger.debug("Discard old session: {}", session.getId());
        final Map<String, Object> attributes = new HashMap<>();
        Collections.list(session.getAttributeNames()).forEach(k -> attributes.put(k, session.getAttribute(k)));
        session.invalidate();
        final HttpSession newSession = request.getSession(true);
        logger.debug("And copy all data to the new one: {}", newSession.getId());
        attributes.forEach((k, v) -> newSession.setAttribute(k, v));
        return true;
    }
}
