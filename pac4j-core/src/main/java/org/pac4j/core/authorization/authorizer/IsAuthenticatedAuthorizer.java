package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;

/**
 * The user must be authenticated. This authorizer should never be necessary unless using the {@link org.pac4j.core.client.direct.AnonymousClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class IsAuthenticatedAuthorizer<U extends CommonProfile> extends SingleProfileAuthorizer<U> {

    private String redirectionUrl;

    public IsAuthenticatedAuthorizer() {}

    public IsAuthenticatedAuthorizer(final String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }

    @Override
    public boolean isProfileAuthorized(final WebContext context, final U profile) throws RequiresHttpAction {

        if (profile != null && !(profile instanceof AnonymousProfile)) {
            return true;
        } else {
            if (this.redirectionUrl != null) {
                throw RequiresHttpAction.redirect("user should be authenticated", context, this.redirectionUrl);
            } else {
                return false;
            }
        }
    }

    public String getRedirectionUrl() {
        return redirectionUrl;
    }

    public void setRedirectionUrl(String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }
}
