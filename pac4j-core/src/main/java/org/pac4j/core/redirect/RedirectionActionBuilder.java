package org.pac4j.core.redirect;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.factory.ProfileManagerFactory;

import java.util.Optional;

/**
 * Return the redirection action to perform.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
@FunctionalInterface
public interface RedirectionActionBuilder {

    /**
     * Attribute name typically expected as an http request attribute
     * that controls whether authentication should be forced.
     * This will get translated to the appropriate protocol
     * for each relevant builder.
     */
    String ATTRIBUTE_FORCE_AUTHN = "ForceAuthn";

    /**
     * Attribute name typically expected as an http request attribute
     * that controls whether authentication should be passive.
     * This will get translated to the appropriate protocol
     * for each relevant builder.
     */
    String ATTRIBUTE_PASSIVE = "Passive";

    /**
     * Return the appropriate "redirection" action.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param profileManagerFactory the profile manager factory
     * @return the "redirection" action (optional)
     */
    Optional<RedirectionAction> getRedirectionAction(WebContext context, SessionStore sessionStore,
                                                     ProfileManagerFactory profileManagerFactory);
}
