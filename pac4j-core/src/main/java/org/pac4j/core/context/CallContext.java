package org.pac4j.core.context;

import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.factory.ProfileManagerFactory;

/**
 * The context of a call.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
public record CallContext(WebContext webContext, SessionStore sessionStore, ProfileManagerFactory profileManagerFactory) {

    public CallContext(final WebContext webContext, final SessionStore sessionStore) {
        this(webContext, sessionStore, ProfileManagerFactory.DEFAULT);
    }
}
