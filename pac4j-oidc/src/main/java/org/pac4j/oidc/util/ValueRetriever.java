package org.pac4j.oidc.util;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.oidc.client.OidcClient;

import java.util.Optional;

/**
 * ValueRetriever retrieves a given value from the {@link WebContext}. It can
 * either read the value from the session store or use some method to regenerate
 * the value. This is used to validate values such as the {@link State}, for
 * CSRF mitigation or the {@link CodeVerifier} for PKCE.
 *
 * @author Martin Hansen
 * @author Emond Papegaaij
 * @since 4.0.3
 */
@FunctionalInterface
public interface ValueRetriever {

    Optional<Object> retrieve(CallContext ctx, String key, OidcClient client);
}
