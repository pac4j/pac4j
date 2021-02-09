package org.pac4j.oauth.profile.creator;

import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth.OAuthService;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.credentials.OAuth20Credentials;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * OAuth 2.0 profile creator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth20ProfileCreator extends OAuthProfileCreator {

    public OAuth20ProfileCreator(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected OAuth2AccessToken getAccessToken(final Credentials credentials) {
        return ((OAuth20Credentials) credentials).getAccessToken();
    }

    @Override
    protected void addAccessTokenToProfile(final UserProfile profile, final Token tok) {
        if (profile != null) {
            final var token = ((OAuth2AccessToken) tok).getAccessToken();
            logger.debug("add access_token: {} to profile", token);
            ((OAuth20Profile) profile).setAccessToken(token);
        }
    }

    @Override
    protected void signRequest(final OAuthService service, final Token token, final OAuthRequest request) {
        ((OAuth20Service) service).signRequest((OAuth2AccessToken) token, request);
        final var accessToken = ((OAuth2AccessToken) token).getAccessToken();
        if (this.configuration.isTokenAsHeader()) {
            request.addHeader(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BEARER_HEADER_PREFIX + accessToken);
        }
        if (Verb.POST.equals(request.getVerb())) {
            request.addParameter(OAuthConfiguration.OAUTH_TOKEN, accessToken);
        }
    }
}
