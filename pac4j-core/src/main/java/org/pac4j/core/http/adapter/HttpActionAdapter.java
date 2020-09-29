package org.pac4j.core.http.adapter;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.HttpAction;

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
     * @param action the HTTP action
     * @param context the web context
     * @return the specific framework HTTP result
     */
    Object adapt(HttpAction action, WebContext context);
}
