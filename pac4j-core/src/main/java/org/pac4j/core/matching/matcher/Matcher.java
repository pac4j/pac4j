package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * To match requests.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
@FunctionalInterface
public interface Matcher {

    /**
     * Check if the web context matches.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @return whether the web context matches
     */
    boolean matches(WebContext context, SessionStore sessionStore);
}
