package org.pac4j.core.context.session;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

import javax.servlet.http.HttpSession;

/**
 * Store data in the provided JEE session (not the one found in the context).
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
class JEEProvidedSessionStore extends JEESessionStore {

    private final HttpSession session;

    public JEEProvidedSessionStore(final HttpSession session) {
        CommonHelper.assertNotNull("session", session);
        this.session = session;
    }

    @Override
    protected HttpSession getNativeSession(final WebContext context) {
        return session;
    }
}
