package org.pac4j.saml.store;

import org.pac4j.core.context.WebContext;

/**
 * Storage factory which doesn't return any store implementation and disables the message store mechanism.
 */
public class EmptyStoreFactory implements SAMLMessageStoreFactory {

    @Override
    public SAMLMessageStore getMessageStore(final WebContext request) {
        return null;
    }

}
