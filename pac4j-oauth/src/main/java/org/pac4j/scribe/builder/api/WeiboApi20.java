package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.oauth2.bearersignature.BearerSignature;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignatureURIQueryParameter;
import org.pac4j.scribe.extractors.WeiboJsonExtractor;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;

/**
 * This class represents the OAuth API implementation for Weibo using OAuth protocol
 * version 2. It could be part of the Scribe library.
 * <p>More info at: <a href="http://open.weibo.com/wiki/Oauth2/authorize">OAuth2.0</a></p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WeiboApi20 extends DefaultApi20 {

    /** Constant <code>BASE_URL="https://api.weibo.com/oauth2/"</code> */
    public static final String BASE_URL = "https://api.weibo.com/oauth2/";

    /** {@inheritDoc} */
    @Override
    public String getAccessTokenEndpoint() {
        return BASE_URL + "access_token";
    }

    /** {@inheritDoc} */
    @Override
    protected String getAuthorizationBaseUrl() {
        return BASE_URL + "authorize";
    }

    /** {@inheritDoc} */
    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return WeiboJsonExtractor.instance();
    }

    /** {@inheritDoc} */
    @Override
    public BearerSignature getBearerSignature() {
        return BearerSignatureURIQueryParameter.instance();
    }
}
