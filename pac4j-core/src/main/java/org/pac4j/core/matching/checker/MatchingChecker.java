package org.pac4j.core.matching.checker;

import org.pac4j.core.client.Client;
import org.pac4j.core.context.CallContext;
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
     * @param ctx the context
     * @param matcherNames the matchers
     * @param matchersMap the map of matchers
     * @param clients the clients
     * @return whether the web context matches
     */
    boolean matches(CallContext ctx, String matcherNames, Map<String, Matcher> matchersMap, List<Client> clients);
}
