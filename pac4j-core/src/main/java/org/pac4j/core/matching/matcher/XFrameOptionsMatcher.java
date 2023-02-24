package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.CallContext;

/**
 * XFrame options header matcher.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class XFrameOptionsMatcher implements Matcher {

    /** {@inheritDoc} */
    @Override
    public boolean matches(final CallContext ctx) {
        ctx.webContext().setResponseHeader("X-Frame-Options", "DENY");
        return true;
    }
}
