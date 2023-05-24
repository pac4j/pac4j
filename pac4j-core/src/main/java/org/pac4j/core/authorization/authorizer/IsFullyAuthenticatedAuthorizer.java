package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * The user must be fully authenticated (not remembered).
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class IsFullyAuthenticatedAuthorizer extends AbstractCheckAuthenticationAuthorizer {

    /**
     * <p>Constructor for IsFullyAuthenticatedAuthorizer.</p>
     */
    public IsFullyAuthenticatedAuthorizer() {}

    /**
     * <p>Constructor for IsFullyAuthenticatedAuthorizer.</p>
     *
     * @param redirectionUrl a {@link String} object
     */
    public IsFullyAuthenticatedAuthorizer(final String redirectionUrl) {
        super(redirectionUrl);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAuthorized(final WebContext context, final SessionStore sessionStore, final List<UserProfile> profiles) {
        return isAnyAuthorized(context, sessionStore, profiles);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isProfileAuthorized(final WebContext context, final SessionStore sessionStore, final UserProfile profile) {
        return profile != null && !(profile instanceof AnonymousProfile) && !profile.isRemembered();
    }

    /**
     * <p>isFullyAuthenticated.</p>
     *
     * @return a {@link IsFullyAuthenticatedAuthorizer} object
     */
    public static IsFullyAuthenticatedAuthorizer isFullyAuthenticated() {
        return new IsFullyAuthenticatedAuthorizer();
    }
}
