package org.pac4j.core.matching;

import org.pac4j.core.context.WebContext;

import java.util.Map;

/**
 * The way to check requests matching.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public interface MatchingChecker {

    /**
     * Check if the web context matches.
     *
     * @param context the web context
     * @param matcherNames the matchers
     * @param matchersMap the map of matchers
     * @return whether the web context matches
     */
    boolean matches(WebContext context, String matcherNames, Map<String, Matcher> matchersMap);
}
