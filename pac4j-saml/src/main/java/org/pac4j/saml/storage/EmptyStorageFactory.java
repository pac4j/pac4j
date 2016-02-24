package org.pac4j.saml.storage;

import org.pac4j.core.context.WebContext;

/**
 * Storage factory which doesn't return any storage implementation and disables the message storage mechanism.
 */
public class EmptyStorageFactory implements SAMLMessageStorageFactory {

    @Override
    public SAMLMessageStorage getMessageStorage(final WebContext request) {
        return null;
    }

}
