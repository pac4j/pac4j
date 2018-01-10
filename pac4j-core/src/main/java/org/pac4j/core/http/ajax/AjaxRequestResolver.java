package org.pac4j.core.http.ajax;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectAction;

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

    /**
     * Build an AJAX reponse.
     *
     * @param url the redirection URL if it was not an AJAX request
     * @param context the web context
     * @return the AJAX response
     */
    RedirectAction buildAjaxResponse(String url, WebContext context);
}
