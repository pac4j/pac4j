package org.pac4j.oauth.credentials.extractor;

import com.github.scribejava.core.utils.OAuthEncoder;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.pac4j.oauth.exception.OAuthCredentialsException;

import java.util.Optional;

/**
 * OAuth 2.0 credentials extractor.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth20CredentialsExtractor extends OAuthCredentialsExtractor {

    /**
     * <p>Constructor for OAuth20CredentialsExtractor.</p>
     *
     * @param configuration a {@link OAuth20Configuration} object
     * @param client a {@link IndirectClient} object
     */
    public OAuth20CredentialsExtractor(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    /** {@inheritDoc} */
    @Override
    protected Optional<Credentials> getOAuthCredentials(final WebContext context, final SessionStore sessionStore) {
        if (((OAuth20Configuration) configuration).isWithState()) {

            val stateParameter = context.getRequestParameter(OAuth20Configuration.STATE_REQUEST_PARAMETER);

            if (stateParameter.isPresent()) {
                val stateSessionAttributeName = this.client.getStateSessionAttributeName();
                val sessionState = (String) sessionStore.get(context, stateSessionAttributeName).orElse(null);
                // clean from session after retrieving it
                sessionStore.set(context, stateSessionAttributeName, null);
                logger.debug("sessionState: {} / stateParameter: {}", sessionState, stateParameter);
                if (!stateParameter.get().equals(sessionState)) {
                    val message = "State parameter mismatch: session expired or possible threat of cross-site request forgery";
                    throw new OAuthCredentialsException(message);
                }
            } else {
                val message = "Missing state parameter: session expired or possible threat of cross-site request forgery";
                throw new OAuthCredentialsException(message);
            }

        }

        val codeParameter = context.getRequestParameter(OAuth20Configuration.OAUTH_CODE);
        if (codeParameter.isPresent()) {
            val code = OAuthEncoder.decode(codeParameter.get());
            logger.debug("code: {}", code);
            return Optional.of(new OAuth20Credentials(code));
        } else {
            logger.debug("No credential found");
            return Optional.empty();
        }
    }
}
