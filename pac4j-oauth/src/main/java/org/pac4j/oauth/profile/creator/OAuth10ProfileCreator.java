package org.pac4j.oauth.profile.creator;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuthService;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.credentials.OAuth10Credentials;
import org.pac4j.oauth.profile.OAuth10Profile;

/**
 * OAuth 1.0 profile creator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth10ProfileCreator<U extends OAuth10Profile>
    extends OAuthProfileCreator<OAuth10Credentials, U, OAuth10Configuration, OAuth1AccessToken> {

    public OAuth10ProfileCreator(final OAuth10Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected OAuth1AccessToken getAccessToken(final OAuth10Credentials credentials) {
        return credentials.getAccessToken();
    }

    @Override
    protected void addAccessTokenToProfile(final U profile, final OAuth1AccessToken accessToken) {
        if (profile != null) {
            final String token = accessToken.getToken();
            logger.debug("add access_token: {} to profile", token);
            profile.setAccessToken(token);
            profile.setAccessSecret(accessToken.getTokenSecret());
        }
    }

    @Override
    protected void signRequest(final OAuthService<OAuth1AccessToken> service, final OAuth1AccessToken token, final OAuthRequest request) {
        service.signRequest(token, request);
        if (this.configuration.isTokenAsHeader()) {
            request.addHeader("Authorization", "Bearer " + token.getToken());
        }
    }
}
