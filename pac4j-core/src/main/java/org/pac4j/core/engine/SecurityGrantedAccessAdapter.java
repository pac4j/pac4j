package org.pac4j.core.engine;

import org.pac4j.core.context.WebContext;

/**
 * Success adapter.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public interface SecurityGrantedAccessAdapter<R, C extends WebContext> {

    /**
     * Adapt the current successful action as the expected result.
     *
     * @param context the web context
     * @param parameters additional parameters
     * @return an adapted result
     * @throws Throwable any exception / error
     */
    R adapt(C context, Object... parameters) throws Throwable;
}
