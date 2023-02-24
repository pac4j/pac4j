package org.pac4j.core.authorization.authorizer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.HttpActionHelper;

/**
 * Check the authentication of the user.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
@ToString
@Getter
@Setter
public abstract class AbstractCheckAuthenticationAuthorizer extends ProfileAuthorizer {

    private String redirectionUrl;

    /**
     * <p>Constructor for AbstractCheckAuthenticationAuthorizer.</p>
     */
    public AbstractCheckAuthenticationAuthorizer() {}

    /**
     * <p>Constructor for AbstractCheckAuthenticationAuthorizer.</p>
     *
     * @param redirectionUrl a {@link java.lang.String} object
     */
    public AbstractCheckAuthenticationAuthorizer(final String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean handleError(final WebContext context, final SessionStore sessionStore) {
        if (this.redirectionUrl != null) {
            throw HttpActionHelper.buildRedirectUrlAction(context, this.redirectionUrl);
        } else {
            return false;
        }
    }
}
