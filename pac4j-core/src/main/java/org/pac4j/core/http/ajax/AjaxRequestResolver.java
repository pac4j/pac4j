package org.pac4j.core.http.ajax;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.RedirectionAction;

/**
 * Compute if a HTTP request is an AJAX one and the appropriate response.
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

    /**
     * Build an AJAX reponse.
     *
     * @param action the original action
     * @param context the web context
     * @return the AJAX response
     */
    HttpAction buildAjaxResponse(RedirectionAction action, WebContext context);
}
