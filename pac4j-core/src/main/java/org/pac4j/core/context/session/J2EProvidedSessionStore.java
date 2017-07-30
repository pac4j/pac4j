package org.pac4j.core.context.session;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.util.CommonHelper;

import javax.servlet.http.HttpSession;

/**
 * Store data in the provided J2E session (not the one found in the context).
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
class J2EProvidedSessionStore extends J2ESessionStore {

    private final HttpSession session;

    public J2EProvidedSessionStore(final HttpSession session) {
        CommonHelper.assertNotNull("session", session);
        this.session = session;
    }

    @Override
    protected HttpSession getHttpSession(final J2EContext context) {
        return session;
    }
}

