package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;

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
    public boolean isAuthorized(final WebContext context, final List<U> profiles) throws HttpAction {
        return isAllAuthorized(context, profiles);
    }

    @Override
    public boolean isProfileAuthorized(final WebContext context, final U profile) throws HttpAction {
        return profile == null || profile instanceof AnonymousProfile;
    }

    @Override
    protected String getErrorMessage() {
        return "user should be anonymous";
    }
}
