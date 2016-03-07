package org.pac4j.saml.storage;

import org.pac4j.core.context.WebContext;

/**
 * Factories implementing this interface provide services for storage and retrieval of SAML messages for
 * e.g. verification of retrieved responses.
 */
public interface SAMLMessageStorageFactory {

    /**
     * Provides message storage related to the given request.
     *
     * @param request currently processed context
     * @return storage objects
     */
    SAMLMessageStorage getMessageStorage(WebContext request);

}
