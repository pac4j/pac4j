package org.pac4j.oauth.credentials.extractor;

import com.github.scribejava.core.utils.OAuthEncoder;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
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
public class OAuth20CredentialsExtractor extends OAuthCredentialsExtractor<OAuth20Credentials, OAuth20Configuration> {

    public OAuth20CredentialsExtractor(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected Optional<OAuth20Credentials> getOAuthCredentials(final WebContext context) {
        if (configuration.isWithState()) {

            final String stateParameter = context.getRequestParameter(OAuth20Configuration.STATE_REQUEST_PARAMETER);

            if (CommonHelper.isNotBlank(stateParameter)) {
                final String stateSessionAttributeName = this.configuration.getStateSessionAttributeName(client.getName());
                final String sessionState = (String) context.getSessionStore().get(context, stateSessionAttributeName);
                // clean from session after retrieving it
                context.getSessionStore().set(context, stateSessionAttributeName, null);
                logger.debug("sessionState: {} / stateParameter: {}", sessionState, stateParameter);
                if (!stateParameter.equals(sessionState)) {
                    final String message = "State parameter mismatch: session expired or possible threat of cross-site request forgery";
                    throw new OAuthCredentialsException(message);
                }
            } else {
                final String message = "Missing state parameter: session expired or possible threat of cross-site request forgery";
                throw new OAuthCredentialsException(message);
            }

        }

        final String codeParameter = context.getRequestParameter(OAuth20Configuration.OAUTH_CODE);
        if (codeParameter != null) {
            final String code = OAuthEncoder.decode(codeParameter);
            logger.debug("code: {}", code);
            return Optional.of(new OAuth20Credentials(code));
        } else {
            final String message = "No credential found";
            throw new OAuthCredentialsException(message);
        }
    }
}
