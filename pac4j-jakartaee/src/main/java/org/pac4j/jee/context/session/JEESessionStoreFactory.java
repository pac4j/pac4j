package org.pac4j.jee.context.session;

import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.context.session.SessionStoreFactory;

/**
 * Build a JEE session store from parameters.
 *
 * @author Jerome LELEU
 * @since 5.5.0
 */
public class JEESessionStoreFactory implements SessionStoreFactory {

    /** Constant <code>INSTANCE</code> */
    public static final SessionStoreFactory INSTANCE = new JEESessionStoreFactory();

    /**
     * {@inheritDoc}
     *
     * Create the session store.
     */
    @Override
    public SessionStore newSessionStore(final FrameworkParameters parameters) {
        return JEESessionStore.INSTANCE;
    }
}
