package org.pac4j.oidc.util;

import org.pac4j.core.context.CallContext;
import org.pac4j.oidc.client.OidcClient;

import java.util.Optional;

/**
 * ValueRetriever retrieves a given value from the {@link org.pac4j.core.context.WebContext}. It can
 * either read the value from the session store or use some method to regenerate
 * the value. This is used to validate values such as the {@link com.nimbusds.oauth2.sdk.id.State}, for
 * CSRF mitigation or the {@link com.nimbusds.oauth2.sdk.pkce.CodeVerifier} for PKCE.
 *
 * @author Martin Hansen
 * @author Emond Papegaaij
 * @since 4.0.3
 */
@FunctionalInterface
public interface ValueRetriever {

    /**
     * <p>retrieve.</p>
     *
     * @param ctx a {@link org.pac4j.core.context.CallContext} object
     * @param key a {@link java.lang.String} object
     * @param client a {@link org.pac4j.oidc.client.OidcClient} object
     * @return a {@link java.util.Optional} object
     */
    Optional<Object> retrieve(CallContext ctx, String key, OidcClient client);
}
