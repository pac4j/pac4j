package org.pac4j.scribe.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.pac4j.scribe.builder.api.WechatApi20;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuthAsyncRequestCallback;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * As of scribejava 5.3, the enumeration class ClientAuthenticationType does not support inheritance,
 * and can not complete the client authentication of wechat.
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WechatService extends OAuth20Service {
    
    private final String apiKey;
    private final String apiSecrect;

    /**
     * Default constructor
     *
     * @param api    OAuth2.0 api information
     * @param apiKey the API key
     * @param apiSecret the API secret
     * @param callback the callback URL
     * @param scope the scope
     * @param responseType the response type
     * @param userAgent the user agent
     * @param httpClientConfig the HTTP client configuration
     * @param httpClient  the HTTP client
     * 
     */
    public WechatService(final DefaultApi20 api, final String apiKey, final String apiSecret,
                         final String callback, final String scope,
                         final String responseType, final String userAgent,
                         final HttpClientConfig httpClientConfig, final HttpClient httpClient) {
        super(api, apiKey, apiSecret, callback, scope, responseType, null, userAgent, httpClientConfig, httpClient);
        this.apiKey = apiKey;
        this.apiSecrect = apiSecret;
    }

    @Override
    public <R> Future<R> execute(final OAuthRequest request, final OAuthAsyncRequestCallback<R> callback,
                                 final OAuthRequest.ResponseConverter<R> converter) {
        final OAuthRequest authRequest = addClientAuthentication(request);
        return super.execute(authRequest, callback, converter);
    }

    @Override
    public Response execute(final OAuthRequest request)
        throws InterruptedException, ExecutionException, IOException {
        final OAuthRequest authRequest = addClientAuthentication(request);
        return super.execute(authRequest);
    }

    private OAuthRequest addClientAuthentication(final OAuthRequest request) {
        request.addParameter(WechatApi20.APPID, this.apiKey);
        request.addParameter(WechatApi20.SECRET, this.apiSecrect);
        return request;
    }
}
