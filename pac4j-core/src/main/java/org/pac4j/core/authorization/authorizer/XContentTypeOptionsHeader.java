package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * XContent type options header.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class XContentTypeOptionsHeader implements Authorizer<UserProfile> {

    @Override
    public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles) throws RequiresHttpAction {
        context.setResponseHeader("X-Content-Type-Options", "nosniff");
        return true;
    }
}
