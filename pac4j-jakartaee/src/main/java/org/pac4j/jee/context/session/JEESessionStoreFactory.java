package org.pac4j.jee.context.session;

import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.context.session.SessionStoreFactory;

/**
 * Build a JEE session store from parameters.
 *
 * @author Jerome LELEU
 * @since 5.5.0
 */
public class JEESessionStoreFactory implements SessionStoreFactory {

    public static final JEESessionStoreFactory INSTANCE = new JEESessionStoreFactory();

    private static final JEESessionStore JEE_SESSION_STORE = new JEESessionStore();

    /**
     * Create the session store.
     *
     * @param parameters the parameters (expected: request, response)
     * @return the JEE session store
     */
    @Override
    public SessionStore newSessionStore(final Object... parameters) {
        return JEE_SESSION_STORE;
    }
}
