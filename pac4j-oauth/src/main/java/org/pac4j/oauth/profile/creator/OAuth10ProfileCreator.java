package org.pac4j.oauth.profile.creator;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.oauth.OAuthService;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.credentials.OAuth10Credentials;
import org.pac4j.oauth.profile.OAuth10Profile;

/**
 * OAuth 1.0 profile creator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth10ProfileCreator extends OAuthProfileCreator {

    /**
     * <p>Constructor for OAuth10ProfileCreator.</p>
     *
     * @param configuration a {@link OAuth10Configuration} object
     * @param client a {@link IndirectClient} object
     */
    public OAuth10ProfileCreator(final OAuth10Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    /** {@inheritDoc} */
    @Override
    protected OAuth1AccessToken getAccessToken(final Credentials credentials) {
        // we assume the access token only has been passed: it can be a bearer call (HTTP client)
        if (credentials instanceof TokenCredentials) {
            val accessToken = ((TokenCredentials) credentials).getToken();
            return new OAuth1AccessToken(accessToken, null);
        }
        // regular OAuth flow
        return ((OAuth10Credentials) credentials).getAccessToken();
    }

    /** {@inheritDoc} */
    @Override
    protected void addTokenToProfile(final UserProfile userProfile, final Token tok) {
        val profile = (OAuth10Profile) userProfile;
        val accessToken = (OAuth1AccessToken) tok;
        if (profile != null) {
            val token = accessToken.getToken();
            logger.debug("add access_token: {} to profile", token);
            profile.setAccessToken(token);
            profile.setAccessSecret(accessToken.getTokenSecret());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void signRequest(final OAuthService service, final Token tok, final OAuthRequest request) {
        val token = (OAuth1AccessToken) tok;
        ((OAuth10aService) service).signRequest(token, request);
        if (this.configuration.isTokenAsHeader()) {
            request.addHeader("Authorization", "Bearer " + token.getToken());
        }
    }
}
