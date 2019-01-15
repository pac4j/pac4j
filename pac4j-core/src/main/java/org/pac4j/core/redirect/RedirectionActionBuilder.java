package org.pac4j.core.redirect;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.RedirectionAction;

import java.util.Optional;

/**
 * Return the "redirection" action to perform.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public interface RedirectionActionBuilder {

    /**
     * Return the appropriate "redirection" action.
     *
     * @param context the web context
     * @return the "redirection" action (optional)
     */
    Optional<RedirectionAction> redirect(WebContext context);
}
