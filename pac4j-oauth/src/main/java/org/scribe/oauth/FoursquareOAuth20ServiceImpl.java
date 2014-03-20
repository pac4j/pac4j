package org.scribe.oauth;


import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;

public class FoursquareOAuth20ServiceImpl extends ProxyOAuth20ServiceImpl {

    public FoursquareOAuth20ServiceImpl(final DefaultApi20 api, final OAuthConfig config, final int connectTimeout,
                                      final int readTimeout, final String proxyHost, final int proxyPort) {
        super(api, config, connectTimeout, readTimeout, proxyHost, proxyPort);
    }

    @Override
    public void signRequest(final Token accessToken, final OAuthRequest request) {
        request.addQuerystringParameter("oauth_token", accessToken.getToken());
    }
}
