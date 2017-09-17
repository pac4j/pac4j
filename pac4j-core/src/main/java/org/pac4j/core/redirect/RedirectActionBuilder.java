package org.pac4j.core.redirect;

import org.pac4j.core.context.WebContext;

/**
 * Return a redirection to perfom.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public interface RedirectActionBuilder {

    /**
     * Return a redirect action for the web context.
     *
     * @param context the web context
     * @return the redirect action
     */
    RedirectAction redirect(WebContext context);
}
