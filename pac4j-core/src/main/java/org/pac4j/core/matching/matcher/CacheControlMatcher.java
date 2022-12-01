package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * Cache control header matcher.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class CacheControlMatcher implements Matcher {

    @Override
    public boolean matches(final WebContext context, final SessionStore sessionStore) {
        final var url = context.getFullRequestURL().toLowerCase();
        if (!url.endsWith(".css")
                && !url.endsWith(".js")
                && !url.endsWith(".png")
                && !url.endsWith(".jpg")
                && !url.endsWith(".ico")
                && !url.endsWith(".jpeg")
                && !url.endsWith(".bmp")
                && !url.endsWith(".gif")) {
            context.setResponseHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
            context.setResponseHeader("Pragma", "no-cache");
            context.setResponseHeader("Expires", "0");
        }
        return true;
    }
}
