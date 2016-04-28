package org.pac4j.scribe.oauth;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.AbstractRequest;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * This service is dedicated for Foursquare service using OAuth protocol version 2.0.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public final class Foursquare20Service extends OAuth20Service {

    public Foursquare20Service(DefaultApi20 api, OAuthConfig config) {
        super(api, config);
    }

    @Override
    public void signRequest(final OAuth2AccessToken accessToken, final AbstractRequest request) {
        request.addQuerystringParameter("oauth_token", accessToken.getAccessToken());
    }
}
