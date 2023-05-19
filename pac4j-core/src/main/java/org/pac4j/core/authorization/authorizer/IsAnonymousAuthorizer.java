package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
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

    /**
     * <p>Constructor for IsAnonymousAuthorizer.</p>
     */
    public IsAnonymousAuthorizer() {}

    /**
     * <p>Constructor for IsAnonymousAuthorizer.</p>
     *
     * @param redirectionUrl a {@link String} object
     */
    public IsAnonymousAuthorizer(final String redirectionUrl) {
        super(redirectionUrl);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAuthorized(final WebContext context, final SessionStore sessionStore, final List<UserProfile> profiles) {
        return isAllAuthorized(context, sessionStore, profiles);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isProfileAuthorized(final WebContext context, final SessionStore sessionStore, final UserProfile profile) {
        return profile == null || profile instanceof AnonymousProfile;
    }

    /**
     * <p>isAnonymous.</p>
     *
     * @return a {@link IsAnonymousAuthorizer} object
     */
    public static IsAnonymousAuthorizer isAnonymous() {
        return new IsAnonymousAuthorizer();
    }
}
