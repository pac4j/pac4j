package org.pac4j.oauth.profile.foursquare;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

/**
 * A specific Foursquare profile creator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class FoursquareProfileCreator extends OAuth20ProfileCreator<FoursquareProfile> {

    public FoursquareProfileCreator(final OAuth20Configuration configuration) {
        super(configuration);
    }

    @Override
    protected void signRequest(final OAuth2AccessToken accessToken, final OAuthRequest request) {
        request.addQuerystringParameter(OAuthConfiguration.OAUTH_TOKEN, accessToken.getAccessToken());
    }
}
