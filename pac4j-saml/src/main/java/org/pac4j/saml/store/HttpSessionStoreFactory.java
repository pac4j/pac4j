package org.pac4j.saml.store;

import org.pac4j.core.context.WebContext;

/**
 * Default store factory which provides HTTP Session store.
 */
public class HttpSessionStoreFactory implements SAMLMessageStoreFactory {

    @Override
    public SAMLMessageStore getMessageStore(final WebContext request) {
        return new HttpSessionStore(request);
    }

}
