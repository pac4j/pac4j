package org.pac4j.oauth.credentials.extractor;

import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.utils.OAuthEncoder;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.credentials.OAuth10Credentials;
import org.pac4j.oauth.exception.OAuthCredentialsException;

import java.util.Optional;

/**
 * OAuth 1.0 credentials extractor.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth10CredentialsExtractor extends OAuthCredentialsExtractor<OAuth10Credentials, OAuth10Configuration> {

    public OAuth10CredentialsExtractor(final OAuth10Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected Optional<OAuth10Credentials> getOAuthCredentials(final WebContext context) {
        final Optional<String> tokenParameter = context.getRequestParameter(OAuth10Configuration.OAUTH_TOKEN);
        final Optional<String> verifierParameter = context.getRequestParameter(OAuth10Configuration.OAUTH_VERIFIER);
        if (tokenParameter.isPresent() && verifierParameter.isPresent()) {
            // get request token from session
            final OAuth1RequestToken tokenSession = (OAuth1RequestToken) context
                .getSessionStore().get(context, configuration.getRequestTokenSessionAttributeName(client.getName())).orElse(null);
            logger.debug("tokenRequest: {}", tokenSession);
            final String token = OAuthEncoder.decode(tokenParameter.get());
            final String verifier = OAuthEncoder.decode(verifierParameter.get());
            logger.debug("token: {} / verifier: {}", token, verifier);
            return Optional.of(new OAuth10Credentials(tokenSession, token, verifier));
        } else {
            final String message = "No credential found";
            throw new OAuthCredentialsException(message);
        }
    }
}
