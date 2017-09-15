package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * An authorizer to require all the elements.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public abstract class AbstractRequireAllAuthorizer<E extends Object, U extends CommonProfile>
    extends AbstractRequireElementAuthorizer<E, U> {

    @Override
    protected boolean isProfileAuthorized(final WebContext context, final U profile) {
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
}
