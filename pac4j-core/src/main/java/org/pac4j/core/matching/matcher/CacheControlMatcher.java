package org.pac4j.core.matching.matcher;

import lombok.val;
import org.pac4j.core.context.CallContext;

/**
 * Cache control header matcher.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class CacheControlMatcher implements Matcher {

    /** {@inheritDoc} */
    @Override
    public boolean matches(final CallContext ctx) {
        val webContext = ctx.webContext();
        val url = webContext.getFullRequestURL().toLowerCase();
        if (!url.endsWith(".css")
                && !url.endsWith(".js")
                && !url.endsWith(".png")
                && !url.endsWith(".jpg")
                && !url.endsWith(".ico")
                && !url.endsWith(".jpeg")
                && !url.endsWith(".bmp")
                && !url.endsWith(".gif")) {
            webContext.setResponseHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
            webContext.setResponseHeader("Pragma", "no-cache");
            webContext.setResponseHeader("Expires", "0");
        }
        return true;
    }
}
