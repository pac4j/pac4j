package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

/**
 * An authorizer to require any of the elements.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public abstract class AbstractRequireAnyAuthorizer<E extends Object> extends AbstractRequireElementAuthorizer<E> {

    @Override
    protected boolean isProfileAuthorized(final WebContext context, final SessionStore sessionStore, final UserProfile profile) {
        if (elements == null || elements.isEmpty()) {
            return true;
        }
        for (final var element : elements) {
            if (check(context, sessionStore, profile, element)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "elements", getElements());
    }
}
