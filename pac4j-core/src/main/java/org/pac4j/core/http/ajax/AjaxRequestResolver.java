package org.pac4j.core.http.ajax;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
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
     * @param context the web context
     * @param sessionStore the session store
     * @return whether it is an AJAX request
     */
    boolean isAjax(WebContext context, SessionStore sessionStore);

    /**
     * Build an AJAX reponse.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param profileManagerFactory the profile manager factory
     * @param redirectionActionBuilder the builder of the redirection, is case the redirect URL calculation needs to be performed
     * @return the AJAX response
     */
    HttpAction buildAjaxResponse(WebContext context, SessionStore sessionStore,
                                 ProfileManagerFactory profileManagerFactory, RedirectionActionBuilder redirectionActionBuilder);
}
