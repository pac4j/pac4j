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

    /**
     * Expected parameters: request, response
     *
     * @param parameters the parameters
     * @return the JEE session store
     */
    @Override
    public SessionStore newContext(Object... parameters) {
        return JEESessionStore.INSTANCE;
    }
}
