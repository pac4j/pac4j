package org.pac4j.core.http;

import org.pac4j.core.context.WebContext;

/**
 * Compute if a HTTP request is an AJAX one.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public interface AjaxRequestResolver {

    /**
     * Whether it is an AJAX request.
     *
     * @param context the web context
     * @return whether it is an AJAX request
     */
    boolean isAjax(WebContext context);
}
