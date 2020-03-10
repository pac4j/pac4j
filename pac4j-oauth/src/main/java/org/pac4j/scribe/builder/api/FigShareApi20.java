package org.pac4j.scribe.builder.api;

import java.io.OutputStream;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
*
* @author Vassilis Virvilis
* @since 3.8.0
*/
public class FigShareApi20 extends DefaultApi20 {
    public static class Service extends OAuth20Service {
        public Service(final DefaultApi20 api, final String apiKey, final String apiSecret,
                       final String callback, final String defaultScope, final String responseType,
                       final OutputStream debugStream, final String userAgent,
                       final HttpClientConfig httpClientConfig, final HttpClient httpClient) {
            super(api, apiKey, apiSecret, callback, defaultScope, responseType, debugStream, userAgent, httpClientConfig, httpClient);
        }

        @Override
        protected OAuthRequest createAccessTokenRequest(final AccessTokenRequestParams params) {
            final OAuthRequest request = super.createAccessTokenRequest(params);
            request.addParameter(OAuthConstants.CLIENT_ID, getApiKey());
            request.addParameter(OAuthConstants.CLIENT_SECRET, getApiSecret());
            return request;
        }
    }

    @Override
    public OAuth20Service createService(final String apiKey, final String apiSecret, final String callback,
                                        final String defaultScope, final String responseType,
                                        final OutputStream debugStream, final String userAgent,
                                        final HttpClientConfig httpClientConfig, final HttpClient httpClient) {
        return new Service(this, apiKey, apiSecret, callback, defaultScope, responseType, debugStream, userAgent, httpClientConfig,
                httpClient);
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.figshare.com/v2/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://figshare.com/account/applications/authorize";
    }
}
