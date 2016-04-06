package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;

/**
 * The user must be authenticated and remembered.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class IsRememberedAuthorizer<U extends CommonProfile> extends AbstractCheckAuthenticationAuthorizer<U> {

    public IsRememberedAuthorizer() {}

    public IsRememberedAuthorizer(final String redirectionUrl) {
        super(redirectionUrl);
    }

    @Override
    public boolean isProfileAuthorized(final WebContext context, final U profile) throws RequiresHttpAction {

        if (profile != null && !(profile instanceof AnonymousProfile) && profile.isRemembered()) {
            return true;
        } else {
            return handleError(context, "user should be remembered");
        }
    }
}
