package org.pac4j.oauth.credentials.extractor;

import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.utils.OAuthEncoder;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.credentials.OAuth10Credentials;
import org.pac4j.oauth.exception.OAuthCredentialsException;

/**
 * OAuth 1.0 credentials extractor.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth10CredentialsExtractor extends OAuthCredentialsExtractor<OAuth10Credentials, OAuth10Configuration> {

    public OAuth10CredentialsExtractor(final OAuth10Configuration configuration) {
        super(configuration);
    }

    @Override
    protected OAuth10Credentials getOAuthCredentials(final WebContext context) throws HttpAction, CredentialsException {
        final String tokenParameter = context.getRequestParameter(OAuth10Configuration.OAUTH_TOKEN);
        final String verifierParameter = context.getRequestParameter(OAuth10Configuration.OAUTH_VERIFIER);
        if (tokenParameter != null && verifierParameter != null) {
            // get request token from session
            final OAuth1RequestToken tokenSession = (OAuth1RequestToken) context.getSessionAttribute(configuration.getRequestTokenSessionAttributeName());
            logger.debug("tokenRequest: {}", tokenSession);
            final String token = OAuthEncoder.decode(tokenParameter);
            final String verifier = OAuthEncoder.decode(verifierParameter);
            logger.debug("token: {} / verifier: {}", token, verifier);
            return new OAuth10Credentials(tokenSession, token, verifier, configuration.getClient().getName());
        } else {
            final String message = "No credential found";
            throw new OAuthCredentialsException(message);
        }
    }
}
