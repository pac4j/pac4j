package org.pac4j.scribe.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.pac4j.scribe.builder.api.WechatApi20;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthAsyncRequestCallback;
import com.github.scribejava.core.model.OAuthConfig;
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

    private OAuthConfig config;

    /**
     * Default constructor
     *
     * @param api    OAuth2.0 api information
     * @param config OAuth 2.0 configuration param object
     */
    public WechatService(DefaultApi20 api,
                         OAuthConfig config) {
        super(api, config);
        this.config = config;
    }

    @Override
    public <R> Future<R> execute(OAuthRequest request, OAuthAsyncRequestCallback<R> callback,
                                 OAuthRequest.ResponseConverter<R> converter) {
        request = addClientAuthentication(request);
        return super.execute(request, callback, converter);
    }

    @Override
    public Response execute(OAuthRequest request)
        throws InterruptedException, ExecutionException, IOException {
        request = addClientAuthentication(request);
        return super.execute(request);
    }

    private OAuthRequest addClientAuthentication(OAuthRequest request) {
        request.addParameter(WechatApi20.APPID, config.getApiKey());
        request.addParameter(WechatApi20.SECRET, config.getApiSecret());
        return request;
    }
}
