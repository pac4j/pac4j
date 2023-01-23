package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.CallContext;

/**
 * XSS protection header matcher.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class XSSProtectionMatcher implements Matcher {

    @Override
    public boolean matches(final CallContext ctx) {
        ctx.webContext().setResponseHeader("X-XSS-Protection", "1; mode=block");
        return true;
    }
}
