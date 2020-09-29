package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * The user must be anonymous. To protect resources like a login page.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class IsAnonymousAuthorizer extends AbstractCheckAuthenticationAuthorizer {

    public IsAnonymousAuthorizer() {}

    public IsAnonymousAuthorizer(final String redirectionUrl) {
        super(redirectionUrl);
    }

    @Override
    public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles) {
        return isAllAuthorized(context, profiles);
    }

    @Override
    public boolean isProfileAuthorized(final WebContext context, final UserProfile profile) {
        return profile == null || profile instanceof AnonymousProfile;
    }

    public static IsAnonymousAuthorizer isAnonymous() {
        return new IsAnonymousAuthorizer();
    }
}
