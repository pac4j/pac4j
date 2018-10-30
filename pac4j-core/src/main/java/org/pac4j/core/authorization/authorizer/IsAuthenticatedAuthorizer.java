package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;

/**
 * The user must be authenticated. This authorizer should never be necessary unless using the
 * {@link org.pac4j.core.client.direct.AnonymousClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class IsAuthenticatedAuthorizer<U extends CommonProfile> extends AbstractCheckAuthenticationAuthorizer<U> {

    public IsAuthenticatedAuthorizer() {}

    public IsAuthenticatedAuthorizer(final String redirectionUrl) {
        super(redirectionUrl);
    }

    @Override
    public boolean isAuthorized(final WebContext context, final List<U> profiles) {
        return isAnyAuthorized(context, profiles);
    }

    @Override
    public boolean isProfileAuthorized(final WebContext context, final U profile) {
        return profile != null && !(profile instanceof AnonymousProfile);
    }

    public static <U extends CommonProfile> IsAuthenticatedAuthorizer<U> isAuthenticated() {
        return new IsAuthenticatedAuthorizer<>();
    }
}
