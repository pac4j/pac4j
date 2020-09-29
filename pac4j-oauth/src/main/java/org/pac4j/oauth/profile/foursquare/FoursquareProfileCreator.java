package org.pac4j.oauth.profile.foursquare;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuthService;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

/**
 * A specific Foursquare profile creator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class FoursquareProfileCreator extends OAuth20ProfileCreator {

    public FoursquareProfileCreator(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected void signRequest(final OAuthService service, final Token accessToken, final OAuthRequest request) {
        request.addQuerystringParameter(OAuthConfiguration.OAUTH_TOKEN, ((OAuth2AccessToken) accessToken).getAccessToken());
    }
}
