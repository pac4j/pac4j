package org.pac4j.scribe.builder.api;

import java.util.Map;

import org.pac4j.oauth.client.WechatClient;
import org.pac4j.scribe.extractors.WechatJsonExtractor;
import org.pac4j.scribe.service.WechatService;

import com.github.scribejava.core.builder.api.ClientAuthenticationType;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.builder.api.OAuth2SignatureType;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;


/**
 * This class represents the OAuth API implementation for Tencent Wechat using OAuth protocol version 2.
 * It could be part of the Scribe library.
 * <p>More info at: <a href=
 * "https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316505&token=&lang=zh_CN"
 * >OAuth2.0</a></p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WechatApi20 extends DefaultApi20 {

    public static final String APPID = "appid";
    public static final String SECRET = "secret";
    //WeChat QR code login
    public static final String AUTHORIZE_ENDPOINT_URL_1 = "https://open.weixin.qq.com/connect/qrconnect";
    //WeChat embedded browser,, call native login.
    public static final String AUTHORIZE_ENDPOINT_URL_2 = "https://open.weixin.qq.com/connect/oauth2/authorize";

    public static final String TOKEN_ENDPOINT_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";


    private static class InstanceHolder {
        private static final WechatApi20 INSTANCE = new WechatApi20();
    }

    public static WechatApi20 instance() {
        return WechatApi20.InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return TOKEN_ENDPOINT_URL + "?grant_type=authorization_code";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config, Map<String, String> additionalParams) {
        String authorizationUrl = super.getAuthorizationUrl(config, additionalParams);
        authorizationUrl = authorizationUrl.replace(OAuthConstants.CLIENT_ID, APPID);
        if (config.getScope() != null && config.getScope().contains(
            WechatClient.WechatScope.SNSAPI_LOGIN.toString().toLowerCase())) {
            authorizationUrl = AUTHORIZE_ENDPOINT_URL_1 + authorizationUrl;
        } else {
            authorizationUrl = AUTHORIZE_ENDPOINT_URL_2 + authorizationUrl;
        }
        return authorizationUrl;
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "";
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.GET;
    }

    @Override
    public OAuth2SignatureType getSignatureType() {
        return OAuth2SignatureType.BEARER_URI_QUERY_PARAMETER;
    }

    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return WechatJsonExtractor.instance();
    }

    @Override
    public ClientAuthenticationType getClientAuthenticationType() {
        return ClientAuthenticationType.REQUEST_BODY;
    }

    @Override
    public OAuth20Service createService(OAuthConfig config) {
        return new WechatService(this, config);
    }
}


