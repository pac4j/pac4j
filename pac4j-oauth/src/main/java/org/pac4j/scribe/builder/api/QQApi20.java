package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.OAuth2AccessTokenExtractor;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignature;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignatureURIQueryParameter;
import com.github.scribejava.core.oauth2.clientauthentication.ClientAuthentication;
import com.github.scribejava.core.oauth2.clientauthentication.RequestBodyAuthenticationScheme;


/**
 * This class represents the OAuth API implementation for Tencent QQ Connect using OAuth protocol
 * version 2. It could be part of the Scribe library.
 * <p>More info at: <a href=
 * "http://wiki.connect.qq.com/%E4%BD%BF%E7%94%A8authorization_code%E8%8E%B7%E5%8F%96access_token">
 * OAuth2.0</a></p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class QQApi20 extends DefaultApi20 {

    /** Constant <code>BASE_URL="https://graph.qq.com/oauth2.0"</code> */
    public static final String BASE_URL = "https://graph.qq.com/oauth2.0";
    // Endpont Url.
    /** Constant <code>AUTHORIZE_ENDPOINT_URL="BASE_URL + /authorize"</code> */
    public static final String AUTHORIZE_ENDPOINT_URL = BASE_URL + "/authorize";
    /** Constant <code>TOKEN_ENDPOINT_URL="BASE_URL + /token"</code> */
    public static final String TOKEN_ENDPOINT_URL = BASE_URL + "/token";

    private static class InstanceHolder {
        private static final QQApi20 INSTANCE = new QQApi20();
    }

    /**
     * <p>instance.</p>
     *
     * @return a {@link QQApi20} object
     */
    public static QQApi20 instance() {
        return QQApi20.InstanceHolder.INSTANCE;
    }

    /** {@inheritDoc} */
    @Override
    public String getAccessTokenEndpoint() {
        return TOKEN_ENDPOINT_URL + "?grant_type=authorization_code";
    }

    /** {@inheritDoc} */
    @Override
    protected String getAuthorizationBaseUrl() {
        return AUTHORIZE_ENDPOINT_URL;
    }

    /** {@inheritDoc} */
    @Override
    public Verb getAccessTokenVerb() {
        return Verb.GET;
    }

    /** {@inheritDoc} */
    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return OAuth2AccessTokenExtractor.instance();
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
