package org.pac4j.saml.store;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * Storage factory which doesn't return any store implementation and disables the message store mechanism.
 *
 * @author bidou
 */
public class EmptyStoreFactory implements SAMLMessageStoreFactory {

    /** {@inheritDoc} */
    @Override
    public SAMLMessageStore getMessageStore(final WebContext request, final SessionStore sessionStore) {
        return null;
    }

}
