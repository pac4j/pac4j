package org.pac4j.saml.storage;

import org.pac4j.core.context.WebContext;

/**
 * Default storage factory which provides HTTP Session storage.
 */
public class HttpSessionStorageFactory implements SAMLMessageStorageFactory {

    @Override
    public SAMLMessageStorage getMessageStorage(final WebContext request) {
        return new HttpSessionStorage(request);
    }

}
