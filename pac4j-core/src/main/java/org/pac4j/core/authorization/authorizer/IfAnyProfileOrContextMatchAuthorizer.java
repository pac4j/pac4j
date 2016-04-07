package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;

/**
 * Authorizer which is valid if one of the profile is authorized.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class IfAnyProfileOrContextMatchAuthorizer<U extends CommonProfile> implements Authorizer<U> {

    @Override
    public boolean isAuthorized(final WebContext context, final List<U> profiles) throws RequiresHttpAction {
        for (final U profile : profiles) {
            if (isProfileAuthorized(context, profile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether a specific profile is authorized.
     *
     * @param context the web context
     * @param profile the user profile
     * @return whether a specific profile is authorized
     * @throws RequiresHttpAction whether an additional HTTP action is required
     */
    protected abstract boolean isProfileAuthorized(final WebContext context, final U profile) throws RequiresHttpAction;
}
