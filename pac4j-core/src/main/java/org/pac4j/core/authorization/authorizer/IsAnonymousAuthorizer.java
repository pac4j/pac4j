package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.List;

/**
 * The user must be anonymous. To protect resources like a login page.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class IsAnonymousAuthorizer<U extends CommonProfile> extends AbstractCheckAuthenticationAuthorizer<U> {

    public IsAnonymousAuthorizer() {}

    public IsAnonymousAuthorizer(final String redirectionUrl) {
        super(redirectionUrl);
    }

    @Override
    public boolean isAuthorized(final WebContext context, final List<U> profiles) throws RequiresHttpAction {
        if (CommonHelper.isEmpty(profiles)) {
            return true;
        }
        return super.isAuthorized(context, profiles);
    }

    @Override
    public boolean isProfileAuthorized(final WebContext context, final U profile) throws RequiresHttpAction {

        if (profile == null || profile instanceof AnonymousProfile) {
            return true;
        } else {
            return handleError(context, "user should be anonymous");
        }
    }
}
