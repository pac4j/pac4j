package org.pac4j.jee.context.session;

import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.context.session.SessionStoreFactory;

/**
 * You should upgrade to the new <code>pac4j-jakartaee</code> module.
 *
 * Build a JEE session store from parameters.
 *
 * @author Jerome LELEU
 * @since 5.5.0
 */
@Deprecated
public class JEESessionStoreFactory implements SessionStoreFactory {

    /** Constant <code>INSTANCE</code> */
    public static final SessionStoreFactory INSTANCE = new JEESessionStoreFactory();

    private static final JEESessionStore JEE_SESSION_STORE = new JEESessionStore();

    /**
     * {@inheritDoc}
     *
     * Create the session store.
     */
    @Override
    public SessionStore newSessionStore(final FrameworkParameters parameters) {
        return JEE_SESSION_STORE;
    }
}
