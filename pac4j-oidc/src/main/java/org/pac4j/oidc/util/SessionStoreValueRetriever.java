package org.pac4j.oidc.util;

import org.pac4j.core.context.CallContext;
import org.pac4j.oidc.client.OidcClient;

import java.util.Optional;

/**
 * The default implementation of {@link ValueRetriever} that reads the values
 * from the {@link org.pac4j.core.context.session.SessionStore} in the {@link org.pac4j.core.context.WebContext}.
 *
 * @author Emond Papegaaij
 * @since 4.0.3
 */
public class SessionStoreValueRetriever implements ValueRetriever {

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<Object> retrieve(final CallContext ctx, final String key, final OidcClient client) {
        return ctx.sessionStore().get(ctx.webContext(), key);
    }
}
