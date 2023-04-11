package org.pac4j.core.http.ajax;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.redirect.RedirectionActionBuilder;

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
     * @param ctx the context
     * @return whether it is an AJAX request
     */
    boolean isAjax(CallContext ctx);

    /**
     * Build an AJAX response.
     *
     * @param ctx                      the context
     * @param redirectionActionBuilder the builder of the redirection, is case the redirect URL calculation needs to be performed
     * @return the AJAX response
     */
    HttpAction buildAjaxResponse(CallContext ctx, RedirectionActionBuilder redirectionActionBuilder);
}
