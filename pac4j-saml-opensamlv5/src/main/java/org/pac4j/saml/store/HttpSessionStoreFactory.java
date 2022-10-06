package org.pac4j.saml.store;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * Default store factory which provides HTTP Session store.
 */
public class HttpSessionStoreFactory implements SAMLMessageStoreFactory {

    @Override
    public SAMLMessageStore getMessageStore(final WebContext context, final SessionStore sessionStore) {
        return new HttpSessionStore(context, sessionStore);
    }

}
