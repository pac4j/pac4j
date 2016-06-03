package org.pac4j.core.matching;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;

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
     * @throws HttpAction whether an additional HTTP action is required
     */
    boolean matches(WebContext context) throws HttpAction;
}
