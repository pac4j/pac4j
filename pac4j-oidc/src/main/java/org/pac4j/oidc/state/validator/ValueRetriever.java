package org.pac4j.oidc.state.validator;

import java.util.Optional;

import org.pac4j.core.context.WebContext;
import org.pac4j.oidc.client.OidcClient;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;

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
public interface ValueRetriever {

    Optional<Object> retrieve(String key, OidcClient<?> client, WebContext webContext);
}
