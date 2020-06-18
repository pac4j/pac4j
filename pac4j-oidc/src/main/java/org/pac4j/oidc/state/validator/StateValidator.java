package org.pac4j.oidc.state.validator;

import com.nimbusds.oauth2.sdk.id.State;
import org.pac4j.core.context.WebContext;
import org.pac4j.oidc.client.OidcClient;

/**
 * StateValidator validates the {@link State}, such as for CSRF mitigation
 *
 * @author Martin Hansen
 * @since 4.0.3
 */
public interface StateValidator {

    void validate(State state, OidcClient client, WebContext webContext);
}
