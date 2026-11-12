package org.pac4j.core.http.configurer;

import org.pac4j.core.context.CallContext;

/**
 * Configures an outgoing HTTP request.
 *
 * @param <R> the HTTP request type
 * @author Jerome LELEU
 * @since 6.7.0
 */
@FunctionalInterface
public interface HttpRequestConfigurer<R> {

    /**
     * Configure the request before sending it.
     *
     * @param request the request
     * @param context the call context; its web context and session store may be null outside a web request
     */
    void configure(R request, CallContext context);
}
