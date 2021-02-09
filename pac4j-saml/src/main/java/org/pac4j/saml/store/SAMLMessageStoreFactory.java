package org.pac4j.saml.store;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * Factories implementing this interface provide services for storing and retrieval of SAML messages for
 * e.g. verification of retrieved responses.
 */
@FunctionalInterface
public interface SAMLMessageStoreFactory {

    /**
     * Provides message store related to the given request.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @return store objects
     */
    SAMLMessageStore getMessageStore(WebContext context, SessionStore sessionStore);

}
