package org.pac4j.saml.store;

import org.pac4j.core.context.WebContext;

/**
 * Factories implementing this interface provide services for storing and retrieval of SAML messages for
 * e.g. verification of retrieved responses.
 */
public interface SAMLMessageStoreFactory {

    /**
     * Provides message store related to the given request.
     *
     * @param request currently processed context
     * @return store objects
     */
    SAMLMessageStore getMessageStore(WebContext request);

}
