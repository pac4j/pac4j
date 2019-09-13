package org.pac4j.oauth.profile.figshare;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * A specific FigShare profile creator.
 *
 * @author Vassilis Virvilis
 * @since 3.8.0
 */
public class FigShareProfileCreator extends OAuth20ProfileCreator<FigShareProfile> {
    public FigShareProfileCreator(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected void signRequest(OAuth20Service service, OAuth2AccessToken accessToken, OAuthRequest request) {
        super.signRequest(service, accessToken, request);
        request.addParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getAccessToken());
    }
}
