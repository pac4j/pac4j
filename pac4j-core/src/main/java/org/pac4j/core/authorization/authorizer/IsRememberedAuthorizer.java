package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;

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
    public boolean isAuthorized(final WebContext context, final List<U> profiles) {
        return isAnyAuthorized(context, profiles);
    }

    @Override
    public boolean isProfileAuthorized(final WebContext context, final U profile) {
        return profile != null && !(profile instanceof AnonymousProfile) && profile.isRemembered();
    }

    public static <U extends CommonProfile> IsRememberedAuthorizer<U> isRemembered() {
        return new IsRememberedAuthorizer<>();
    }
}
