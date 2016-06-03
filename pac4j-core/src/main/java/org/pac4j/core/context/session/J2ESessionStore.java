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
    public String getOrCreateSessionId(J2EContext context) {
        return getHttpSession(context).getId();
    }

    @Override
    public Object get(J2EContext context, String key) {
        return getHttpSession(context).getAttribute(key);
    }

    @Override
    public void set(J2EContext context, String key, Object value) {
        getHttpSession(context).setAttribute(key, value);
    }
}
