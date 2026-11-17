package org.pac4j.core.http.interceptor;

import org.pac4j.core.context.CallContext;

/**
 * Intercepts an outgoing HTTP request and its response.
 *
 * The type of the request and of the response depends on the caller: the interceptor casts them to the types
 * it supports.
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
@FunctionalInterface
public interface HttpInterceptor {

    /**
     * Configure the request before sending it.
     *
     * @param request the request
     * @param context the call context; its web context and session store may be null outside a web request
     */
    void configureRequest(Object request, CallContext context);

    /**
     * Handle the response received for a request. When the request must be sent again, it is configured again
     * before, and the response to this second sending is handled as well but never triggers a third one.
     *
     * @param request the request
     * @param response the response
     * @param context the call context; its web context and session store may be null outside a web request
     * @return whether the request must be sent again
     */
    default boolean handleResponse(final Object request, final Object response, final CallContext context) {
        return false;
    }
}
