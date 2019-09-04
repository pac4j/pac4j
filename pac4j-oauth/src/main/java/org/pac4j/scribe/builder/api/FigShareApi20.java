package org.pac4j.scribe.builder.api;

import java.io.OutputStream;

import org.pac4j.scribe.builder.api.GenericApi20;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
*
* @author Vassilis Virvilis
* @since 3.8.0
*/
public class FigShareApi20 extends GenericApi20 {
    public static class Service extends OAuth20Service {
        public Service(DefaultApi20 api, String apiKey, String apiSecret, String callback, String scope, String state, String responseType,
                String userAgent, HttpClientConfig httpClientConfig, HttpClient httpClient) {
            super(api, apiKey, apiSecret, callback, scope, state, responseType, userAgent, httpClientConfig, httpClient);
        }

        @Override
        protected OAuthRequest createAccessTokenRequest(String code) {
            final OAuthRequest request = super.createAccessTokenRequest(code);
            request.addParameter(OAuthConstants.CLIENT_ID, getApiKey());
            request.addParameter(OAuthConstants.CLIENT_SECRET, getApiSecret());
            return request;
        }
    }

    public FigShareApi20(String authUrl, String tokenUrl) {
        super(authUrl, tokenUrl);
    }

    @Override
    public OAuth20Service createService(String apiKey, String apiSecret, String callback, String scope, OutputStream debugStream,
            String state, String responseType, String userAgent, HttpClientConfig httpClientConfig, HttpClient httpClient) {
        return new Service(this, apiKey, apiSecret, callback, scope, state, responseType, userAgent, httpClientConfig, httpClient);
    }
}
