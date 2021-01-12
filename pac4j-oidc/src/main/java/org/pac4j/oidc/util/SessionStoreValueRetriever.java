package org.pac4j.oidc.util;

import java.util.Optional;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.oidc.client.OidcClient;

/**
 * The default implementation of {@link ValueRetriever} that reads the values
 * from the {@link SessionStore} in the {@link WebContext}.
 *
 * @author Emond Papegaaij
 * @since 4.0.3
 */
public class SessionStoreValueRetriever implements ValueRetriever {

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Object> retrieve(final String key, final OidcClient client,
                                     final WebContext webContext, final SessionStore sessionStore) {
        return sessionStore.get(webContext, key);
    }
}
