package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.CallContext;

/**
 * To match requests.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
@FunctionalInterface
public interface Matcher {

    /**
     * Check if the context matches.
     *
     * @param ctx the context
     * @return whether the context matches
     */
    boolean matches(CallContext ctx);
}
