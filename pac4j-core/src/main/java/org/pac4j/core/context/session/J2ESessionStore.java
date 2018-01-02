package org.pac4j.core.context.session;

import org.pac4j.core.context.J2EContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Store data in the J2E web session.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class J2ESessionStore implements SessionStore<J2EContext> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected HttpSession getHttpSession(final J2EContext context) {
        return context.getRequest().getSession();
    }

    @Override
    public String getOrCreateSessionId(final J2EContext context) {
        return getHttpSession(context).getId();
    }

    @Override
    public Object get(final J2EContext context, final String key) {
        return getHttpSession(context).getAttribute(key);
    }

    @Override
    public void set(final J2EContext context, final String key, final Object value) {
        if (value == null) {
            getHttpSession(context).removeAttribute(key);
        } else {
            getHttpSession(context).setAttribute(key, value);
        }
    }

    @Override
    public boolean destroySession(final J2EContext context) {
        getHttpSession(context).invalidate();
        return true;
    }

    @Override
    public Object getTrackableSession(final J2EContext context) {
        return getHttpSession(context);
    }

    @Override
    public SessionStore<J2EContext> buildFromTrackableSession(final J2EContext context, final Object trackableSession) {
        if (trackableSession != null) {
            return new J2EProvidedSessionStore((HttpSession) trackableSession);
        } else {
            return null;
        }
    }

    @Override
    public boolean renewSession(final J2EContext context) {
        final HttpServletRequest request = context.getRequest();
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
