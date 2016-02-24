package org.pac4j.core.http;

import org.pac4j.core.context.WebContext;

/**
 * HTTP action adapter.
 *
 * @author Jerome Leleu
 * @since 1.8.2
 */
public interface HttpActionAdapter {

    /**
     * Adapt the HTTP action.
     *
     * @param code the HTTP action status code
     * @param context the web context
     * @return the specific framework HTTP result
     */
    Object adapt(int code, WebContext context);
}
