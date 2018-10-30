package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;

/**
 * The user must be fully authenticated (not remembered).
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class IsFullyAuthenticatedAuthorizer<U extends CommonProfile> extends AbstractCheckAuthenticationAuthorizer<U> {

    public IsFullyAuthenticatedAuthorizer() {}

    public IsFullyAuthenticatedAuthorizer(final String redirectionUrl) {
        super(redirectionUrl);
    }

    @Override
    public boolean isAuthorized(final WebContext context, final List<U> profiles) {
        return isAnyAuthorized(context, profiles);
    }

    @Override
    public boolean isProfileAuthorized(final WebContext context, final U profile) {
        return profile != null && !(profile instanceof AnonymousProfile) && !profile.isRemembered();
    }

    public static <U extends CommonProfile> IsFullyAuthenticatedAuthorizer<U> isFullyAuthenticated() {
        return new IsFullyAuthenticatedAuthorizer<>();
    }
}
