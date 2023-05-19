package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * The user must be authenticated. This authorizer should never be necessary unless using the
 * {@link org.pac4j.core.client.direct.AnonymousClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class IsAuthenticatedAuthorizer extends AbstractCheckAuthenticationAuthorizer {

    /**
     * <p>Constructor for IsAuthenticatedAuthorizer.</p>
     */
    public IsAuthenticatedAuthorizer() {}

    /**
     * <p>Constructor for IsAuthenticatedAuthorizer.</p>
     *
     * @param redirectionUrl a {@link String} object
     */
    public IsAuthenticatedAuthorizer(final String redirectionUrl) {
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
        return profile != null && !(profile instanceof AnonymousProfile);
    }

    /**
     * <p>isAuthenticated.</p>
     *
     * @return a {@link IsAuthenticatedAuthorizer} object
     */
    public static IsAuthenticatedAuthorizer isAuthenticated() {
        return new IsAuthenticatedAuthorizer();
    }
}
