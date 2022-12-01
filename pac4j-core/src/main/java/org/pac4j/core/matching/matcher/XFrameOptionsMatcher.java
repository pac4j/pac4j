package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * XFrame options header matcher.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class XFrameOptionsMatcher implements Matcher {

    @Override
    public boolean matches(final WebContext context, final SessionStore sessionStore) {
        context.setResponseHeader("X-Frame-Options", "DENY");
        return true;
    }
}
