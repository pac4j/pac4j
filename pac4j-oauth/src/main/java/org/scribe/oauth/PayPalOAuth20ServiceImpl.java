package org.scribe.oauth;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;

/**
 * This service is dedicated for PayPal service using OAuth protocol version 2.0.
 * 
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalOAuth20ServiceImpl extends ProxyOAuth20ServiceImpl {
    
    public PayPalOAuth20ServiceImpl(final DefaultApi20 api, final OAuthConfig config, final int connectTimeout,
                                    final int readTimeout, final String proxyHost, final int proxyPort) {
        super(api, config, connectTimeout, readTimeout, proxyHost, proxyPort, true, true);
    }
    
    @Override
    public void signRequest(final Token accessToken, final OAuthRequest request) {
        // request.addQuerystringParameter(OAuthConstants.CLIENT_ID, this.config.getApiKey());
        // request.addQuerystringParameter(OAuthConstants.CLIENT_SECRET, this.config.getApiSecret());
        request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());
    }
}
