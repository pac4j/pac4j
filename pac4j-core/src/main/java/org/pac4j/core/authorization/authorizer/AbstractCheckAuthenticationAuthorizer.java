package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.RedirectionActionHelper;
import org.pac4j.core.profile.UserProfile;

/**
 * Check the authentication of the user.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class AbstractCheckAuthenticationAuthorizer<U extends UserProfile> extends ProfileAuthorizer<U> {

    private String redirectionUrl;

    public AbstractCheckAuthenticationAuthorizer() {}

    public AbstractCheckAuthenticationAuthorizer(final String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }

    @Override
    protected boolean handleError(final WebContext context) {
        if (this.redirectionUrl != null) {
            throw RedirectionActionHelper.buildRedirectUrlAction(context, this.redirectionUrl);
        } else {
            return false;
        }
    }

    public String getRedirectionUrl() {
        return redirectionUrl;
    }

    public void setRedirectionUrl(final String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }
}
