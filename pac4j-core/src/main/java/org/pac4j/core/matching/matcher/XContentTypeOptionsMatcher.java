package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.WebContext;

/**
 * XContent type options header matcher.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class XContentTypeOptionsMatcher implements Matcher {

    @Override
    public boolean matches(final WebContext context) {
        context.setResponseHeader("X-Content-Type-Options", "nosniff");
        return true;
    }
}
