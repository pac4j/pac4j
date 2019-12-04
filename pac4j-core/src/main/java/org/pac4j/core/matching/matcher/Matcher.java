package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.WebContext;

/**
 * To match requests.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public interface Matcher {

    /**
     * Check if the web context matches.
     *
     * @param context the web context
     * @return whether the web context matches
     */
    boolean matches(WebContext context);
}
