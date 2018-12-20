package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * XFrame options header.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class XFrameOptionsHeader implements Authorizer<UserProfile> {

    @Override
    public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles) {
        context.setResponseHeader("X-Frame-Options", "DENY");
        return true;
    }
}
