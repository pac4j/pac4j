package org.pac4j.oauth.profile.dropbox;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuthService;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

/**
 * DropBox profile creator.
 *
 * @author Matthias Bohlen
 * @since 3.0.0
 */
public class DropBoxProfileCreator extends OAuth20ProfileCreator<DropBoxProfile> {

    public DropBoxProfileCreator(OAuth20Configuration configuration, IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected void signRequest(OAuthService<OAuth2AccessToken> service, OAuth2AccessToken accessToken, OAuthRequest request) {
        request.addHeader(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BEARER_HEADER_PREFIX + accessToken.getAccessToken());
    }

}