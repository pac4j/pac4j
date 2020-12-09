package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

/**
 * An authorizer to require all the elements.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public abstract class AbstractRequireAllAuthorizer<E extends Object> extends AbstractRequireElementAuthorizer<E> {

    @Override
    protected boolean isProfileAuthorized(final WebContext context, final UserProfile profile) {
        if (elements == null || elements.isEmpty()) {
            return true;
        }
        for (final E element : elements) {
            if (!check(context, profile, element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "elements", getElements());
    }
}
