package org.pac4j.oauth.profile.creator;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1Token;
import com.github.scribejava.core.model.OAuthRequest;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.credentials.OAuth10Credentials;
import org.pac4j.oauth.profile.OAuth10Profile;

/**
 * OAuth 1.0 profile creator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth10ProfileCreator<U extends OAuth10Profile> extends OAuthProfileCreator<OAuth10Credentials, U, OAuth10Configuration, OAuth1Token> {

    public OAuth10ProfileCreator(final OAuth10Configuration configuration) {
        super(configuration);
    }

    @Override
    protected OAuth1Token getAccessToken(final OAuth10Credentials credentials) throws HttpAction {
        return credentials.getAccessToken();
    }

    @Override
    protected void addAccessTokenToProfile(final U profile, final OAuth1Token accessToken) {
        if (profile != null) {
            final String token = accessToken.getToken();
            logger.debug("add access_token: {} to profile", token);
            profile.setAccessToken(token);
            profile.setAccessSecret(accessToken.getTokenSecret());
        }
    }

    @Override
    protected void signRequest(final OAuth1Token token, final OAuthRequest request) {
        this.configuration.getService().signRequest((OAuth1AccessToken) token, request);
        if (this.configuration.isTokenAsHeader()) {
            request.addHeader("Authorization", "Bearer " + token.getToken());
        }
    }
}
