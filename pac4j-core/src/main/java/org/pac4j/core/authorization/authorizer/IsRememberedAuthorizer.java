package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * The user must be authenticated and remembered.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class IsRememberedAuthorizer extends AbstractCheckAuthenticationAuthorizer {

    /**
     * <p>Constructor for IsRememberedAuthorizer.</p>
     */
    public IsRememberedAuthorizer() {}

    /**
     * <p>Constructor for IsRememberedAuthorizer.</p>
     *
     * @param redirectionUrl a {@link String} object
     */
    public IsRememberedAuthorizer(final String redirectionUrl) {
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
        return profile != null && !(profile instanceof AnonymousProfile) && profile.isRemembered();
    }

    /**
     * <p>isRemembered.</p>
     *
     * @return a {@link IsRememberedAuthorizer} object
     */
    public static IsRememberedAuthorizer isRemembered() {
        return new IsRememberedAuthorizer();
    }
}
