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

    boolean matches(WebContext context, String matcherName, Map<String, Matcher> matchersMap);
}
