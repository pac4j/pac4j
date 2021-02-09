package org.pac4j.core.matching.checker;

import org.pac4j.core.client.Client;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.matching.matcher.Matcher;

import java.util.List;
import java.util.Map;

/**
 * The way to check requests matching.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
@FunctionalInterface
public interface MatchingChecker {

    /**
     * Check if the web context matches.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param matcherNames the matchers
     * @param matchersMap the map of matchers
     * @param clients the clients
     * @return whether the web context matches
     */
    boolean matches(WebContext context, SessionStore sessionStore, String matcherNames,
                    Map<String, Matcher> matchersMap, List<Client> clients);
}
