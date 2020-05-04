package org.pac4j.core.context;

/**
 * Build a web context from parameters.
 *
 * @author Jerome LELEU
 * @since 4.0.1
 * @param <C> the web context
 */
public interface WebContextFactory<C extends WebContext> {

    C newContext(Object... parameters);
}
