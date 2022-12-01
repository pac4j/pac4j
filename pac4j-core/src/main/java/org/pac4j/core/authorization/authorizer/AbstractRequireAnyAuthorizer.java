package org.pac4j.core.authorization.authorizer;

import lombok.ToString;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;

/**
 * An authorizer to require any of the elements.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
@ToString(callSuper = true)
public abstract class AbstractRequireAnyAuthorizer<E extends Object> extends AbstractRequireElementAuthorizer<E> {

    @Override
    protected boolean isProfileAuthorized(final WebContext context, final SessionStore sessionStore, final UserProfile profile) {
        if (elements == null || elements.isEmpty()) {
            return check(context, sessionStore, profile, null);
        }
        for (val element : elements) {
            if (check(context, sessionStore, profile, element)) {
                return true;
            }
        }
        return false;
    }
}
