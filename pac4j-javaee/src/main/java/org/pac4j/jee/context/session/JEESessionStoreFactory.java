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

    public static final JEESessionStoreFactory INSTANCE = new JEESessionStoreFactory();

    /**
     * Create the session store.
     *
     * @param parameters the JEE parameters
     * @return the JEE session store
     */
    @Override
    public SessionStore newSessionStore(final FrameworkParameters parameters) {
        return JEESessionStore.INSTANCE;
    }
}
