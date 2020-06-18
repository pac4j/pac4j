package org.pac4j.oidc.state.validator;

import com.nimbusds.oauth2.sdk.id.State;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oidc.client.OidcClient;

/**
 * SessionStoreStateValidator
 *
 * Equality checks the {@link State} against the {@link State} stored in the {@link org.pac4j.core.context.session.SessionStore}
 *
 * @author Martin Hansen
 * @since 4.0.3
 */
public class SessionStoreStateValidator implements StateValidator {

    @Override
    public void validate(State state, OidcClient client, WebContext webContext) {
        if (state == null) {
            throw new TechnicalException("Missing state parameter");
        }
        if (!state.equals(webContext.getSessionStore().get(webContext, client.getStateSessionAttributeName()).orElse(null))) {
            throw new TechnicalException("State parameter is different from the one sent in authentication request. "
                + "Session expired or possible threat of cross-site request forgery");
        }
    }
}
