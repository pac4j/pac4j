package org.pac4j.core.context;

/**
 * Build a web context from parameters.
 *
 * @author Jerome LELEU
 * @since 4.0.1
 */
public interface WebContextFactory {

    WebContext newContext(Object... parameters);
}
