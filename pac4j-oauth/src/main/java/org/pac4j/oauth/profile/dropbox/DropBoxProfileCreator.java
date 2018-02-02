package org.pac4j.oauth.profile.dropbox;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuth20Service;
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

    public DropBoxProfileCreator(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected void signRequest(final OAuth20Service service, final OAuth2AccessToken accessToken, final OAuthRequest request) {
        request.addHeader(HttpConstants.CONTENT_TYPE_HEADER, HttpConstants.APPLICATION_JSON);
        request.addHeader(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BEARER_HEADER_PREFIX + accessToken.getAccessToken());
        request.setPayload("null");
    }
}
