package org.pac4j.core.redirect;

import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;

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
     * @throws HttpAction requires a specific HTTP action if necessary
     */
    RedirectAction redirect(final WebContext context) throws HttpAction;
}
