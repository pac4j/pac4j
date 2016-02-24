package org.pac4j.core.matching;

import org.pac4j.core.context.WebContext;

/**
 * To match requests.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public interface Matcher {

    boolean matches(WebContext context);
}
