package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;

/**
 * Check the authentication of the user.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class AbstractCheckAuthenticationAuthorizer<U extends CommonProfile> extends IfAnyProfileOrContextMatchAuthorizer<U> {

    private String redirectionUrl;

    public AbstractCheckAuthenticationAuthorizer() {}

    public AbstractCheckAuthenticationAuthorizer(final String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }

    protected boolean handleError(final WebContext context, final String msg) throws RequiresHttpAction {
        if (this.redirectionUrl != null) {
            throw RequiresHttpAction.redirect(msg, context, this.redirectionUrl);
        } else {
            return false;
        }
    }

    public String getRedirectionUrl() {
        return redirectionUrl;
    }

    public void setRedirectionUrl(String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }
}
