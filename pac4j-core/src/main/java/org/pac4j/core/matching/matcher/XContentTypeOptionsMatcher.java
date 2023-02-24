package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.CallContext;

/**
 * XContent type options header matcher.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class XContentTypeOptionsMatcher implements Matcher {

    /** {@inheritDoc} */
    @Override
    public boolean matches(final CallContext ctx) {
        ctx.webContext().setResponseHeader("X-Content-Type-Options", "nosniff");
        return true;
    }
}
