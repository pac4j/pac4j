package org.pac4j.core.context.session;

/**
 * Build a session store from parameters.
 *
 * @author Jerome LELEU
 * @since 5.5.0
 */
@FunctionalInterface
public interface SessionStoreFactory {

    SessionStore newContext(Object... parameters);
}
