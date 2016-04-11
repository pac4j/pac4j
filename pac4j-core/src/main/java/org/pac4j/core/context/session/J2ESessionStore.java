package org.pac4j.core.context.session;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;

import javax.servlet.http.HttpSession;

/**
 * Store data in the J2E session.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class J2ESessionStore implements SessionStore {

    private HttpSession getHttpSession(final WebContext context) {
        final J2EContext j2EContext = (J2EContext) context;
        return j2EContext.getRequest().getSession();
    }

    @Override
    public String getOrCreateSessionId(WebContext context) {
        return getHttpSession(context).getId();
    }

    @Override
    public Object get(WebContext context, String key) {
        return getHttpSession(context).getAttribute(key);
    }

    @Override
    public void set(WebContext context, String key, Object value) {
        getHttpSession(context).setAttribute(key, value);
    }
}
