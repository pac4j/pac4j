package org.pac4j.core.context.session;

import org.pac4j.core.context.FrameworkParameters;

/**
 * Build a session store from framework parameters.
 *
 * @author Jerome LELEU
 * @since 5.5.0
 */
@FunctionalInterface
public interface SessionStoreFactory {

    /**
     * <p>newSessionStore.</p>
     *
     * @param parameters a {@link FrameworkParameters} object
     * @return a {@link SessionStore} object
     */
    SessionStore newSessionStore(FrameworkParameters parameters);
}
