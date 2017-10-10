package org.pac4j.oauth.profile.dropbox;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

public class DropBoxProfileCreator extends OAuth20ProfileCreator<DropBoxProfile> {

    public DropBoxProfileCreator(OAuth20Configuration configuration) {
        super(configuration);
    }

    @Override
    protected void signRequest(final OAuth2AccessToken accessToken, final OAuthRequest request) {
        request.addHeader(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BEARER_HEADER_PREFIX + accessToken.getAccessToken());
    }

    @Override
    protected OAuthRequest createOAuthRequest(final String url, final Verb verb) {
        return new OAuthRequest(verb, url, this.configuration.getService()) {
            @Override
            protected boolean hasBodyContent() {
                return false;
            }
        };
    }

}
