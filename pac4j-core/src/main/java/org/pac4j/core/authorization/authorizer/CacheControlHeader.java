package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * Cache control header.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class CacheControlHeader implements Authorizer<UserProfile> {

    @Override
    public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles) {
        final String url = context.getFullRequestURL().toLowerCase();
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
