package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignature;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignatureURIQueryParameter;
import com.github.scribejava.core.oauth2.clientauthentication.ClientAuthentication;
import com.github.scribejava.core.oauth2.clientauthentication.RequestBodyAuthenticationScheme;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.WechatClient;
import org.pac4j.scribe.extractors.WechatJsonExtractor;
import org.pac4j.scribe.service.WechatService;

import java.io.OutputStream;
import java.util.Map;

/**
 * This class represents the OAuth API implementation for Tencent Wechat using OAuth protocol version 2.
 * It could be part of the Scribe library.
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WechatApi20 extends DefaultApi20 {

    /** Constant <code>APPID="appid"</code> */
    public static final String APPID = "appid";
    /** Constant <code>SECRET="secret"</code> */
    public static final String SECRET = "secret";
    //WeChat QR code login
    /** Constant <code>AUTHORIZE_ENDPOINT_URL_1="https://open.weixin.qq.com/connect/qrco"{trunked}</code> */
    public static final String AUTHORIZE_ENDPOINT_URL_1 = "https://open.weixin.qq.com/connect/qrconnect";
    //WeChat embedded browser,, call native login.
    /** Constant <code>AUTHORIZE_ENDPOINT_URL_2="https://open.weixin.qq.com/connect/oaut"{trunked}</code> */
    public static final String AUTHORIZE_ENDPOINT_URL_2 = "https://open.weixin.qq.com/connect/oauth2/authorize";

    /** Constant <code>TOKEN_ENDPOINT_URL="https://api.weixin.qq.com/sns/oauth2/ac"{trunked}</code> */
    public static final String TOKEN_ENDPOINT_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";


    private static class InstanceHolder {
        private static final WechatApi20 INSTANCE = new WechatApi20();
    }

    /**
     * <p>instance.</p>
     *
     * @return a {@link WechatApi20} object
     */
    public static WechatApi20 instance() {
        return WechatApi20.InstanceHolder.INSTANCE;
    }

    /** {@inheritDoc} */
    @Override
    public String getAccessTokenEndpoint() {
        return TOKEN_ENDPOINT_URL + "?grant_type=authorization_code";
    }

    /** {@inheritDoc} */
    @Override
    public String getAuthorizationUrl(String responseType, String apiKey, String callback, String scope, String state,
            Map<String, String> additionalParams) {
        var authorizationUrl = super.getAuthorizationUrl(responseType, apiKey, callback, scope, state, additionalParams);
        authorizationUrl = authorizationUrl.replace(OAuthConstants.CLIENT_ID, APPID);
        if (scope != null && scope.contains(
            WechatClient.WechatScope.SNSAPI_LOGIN.toString().toLowerCase())) {
            authorizationUrl = AUTHORIZE_ENDPOINT_URL_1 + authorizationUrl;
        } else {
            authorizationUrl = AUTHORIZE_ENDPOINT_URL_2 + authorizationUrl;
        }
        return authorizationUrl;
    }

    /** {@inheritDoc} */
    @Override
    protected String getAuthorizationBaseUrl() {
        return Pac4jConstants.EMPTY_STRING;
    }

    /** {@inheritDoc} */
    @Override
    public Verb getAccessTokenVerb() {
        return Verb.GET;
    }

    /** {@inheritDoc} */
    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return WechatJsonExtractor.instance();
    }

    /** {@inheritDoc} */
    @Override
    public OAuth20Service createService(String apiKey, String apiSecret, String callback, String defaultScope,
                                                     String responseType, OutputStream debugStream, String userAgent,
                                                     HttpClientConfig httpClientConfig, HttpClient httpClient) {
        return new WechatService(this, apiKey, apiSecret, callback, defaultScope, responseType, userAgent, httpClientConfig, httpClient);
    }

    /** {@inheritDoc} */
    @Override
    public BearerSignature getBearerSignature() {
        return BearerSignatureURIQueryParameter.instance();
    }

    /** {@inheritDoc} */
    @Override
    public ClientAuthentication getClientAuthentication() {
        return RequestBodyAuthenticationScheme.instance();
    }
}
