package org.pac4j.core.matching.matcher.csrf;

import org.pac4j.core.context.WebContext;

/**
 * CSRF token generator.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public interface CsrfTokenGenerator {

    /**
     * Get the CSRF token from the session or create it if it doesn't exist.
     *
     * @param context the current web context
     * @return the CSRF token
     */
    String get(WebContext context);
}
