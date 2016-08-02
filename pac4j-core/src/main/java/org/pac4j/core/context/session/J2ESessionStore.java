package org.pac4j.core.context.session;

import org.pac4j.core.context.J2EContext;

import javax.servlet.http.HttpSession;

/**
 * Store data in the J2E session.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class J2ESessionStore implements SessionStore<J2EContext> {

    private HttpSession getHttpSession(final J2EContext context) {
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
        getHttpSession(context).setAttribute(key, value);
    }

    @Override
    public void invalidateSession(final J2EContext context) {
        getHttpSession(context).invalidate();
    }
}
