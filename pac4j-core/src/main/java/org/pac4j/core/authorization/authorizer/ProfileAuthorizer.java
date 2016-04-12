package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;

/**
 * Authorizer which is valid if one of the profiles is authorized or all the profiles are authorized.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class ProfileAuthorizer<U extends CommonProfile> implements Authorizer<U> {

    /**
     * If all profiles are authorized.
     *
     * @param context the web context
     * @param profiles the user profiles
     * @return whether all profiles are authorized
     * @throws RequiresHttpAction
     */
    public boolean isAllAuthorized(final WebContext context, final List<U> profiles) throws RequiresHttpAction {
        for (final U profile : profiles) {
            if (!isProfileAuthorized(context, profile)) {
                return handleError(context);
            }
        }
        return true;
    }

    /**
     * If any of the profiles is authorized.
     *
     * @param context the web context
     * @param profiles the user profiles
     * @return whether any of the profiles is authorized
     * @throws RequiresHttpAction
     */
    public boolean isAnyAuthorized(final WebContext context, final List<U> profiles) throws RequiresHttpAction {
        for (final U profile : profiles) {
            if (isProfileAuthorized(context, profile)) {
                return true;
            }
        }
        return handleError(context);
    }

    /**
     * Whether a specific profile is authorized.
     *
     * @param context the web context
     * @param profile the user profile
     * @return whether a specific profile is authorized
     * @throws RequiresHttpAction whether an additional HTTP action is required
     */
    protected abstract boolean isProfileAuthorized(WebContext context, U profile) throws RequiresHttpAction;

    /**
     * Handle the error.
     *
     * @param context the web context
     * @return <code>false</code>
     * @throws RequiresHttpAction whether an additional HTTP action is required
     */
    protected boolean handleError(final WebContext context) throws RequiresHttpAction {
        return false;
    }
}
