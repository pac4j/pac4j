package org.pac4j.scribe.extractors;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.scribe.model.WechatToken;

/**
 * This class represents a specific JSON extractor for Wechat Connect using OAuth protocol version 2.
 * It could be part of the Scribe library.
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WechatJsonExtractor extends OAuth2AccessTokenJsonExtractor {

    /**
     * <p>Constructor for WechatJsonExtractor.</p>
     */
    protected WechatJsonExtractor() {
    }

    private static class InstanceHolder {

        private static final WechatJsonExtractor INSTANCE = new WechatJsonExtractor();
    }

    /**
     * <p>instance.</p>
     *
     * @return a {@link org.pac4j.scribe.extractors.WechatJsonExtractor} object
     */
    public static WechatJsonExtractor instance() {
        return WechatJsonExtractor.InstanceHolder.INSTANCE;
    }

    /** {@inheritDoc} */
    @Override
    protected OAuth2AccessToken createToken(String accessToken, String tokenType, Integer expiresIn, String refreshToken, String scope,
                                            JsonNode response, String rawResponse) {
        var openid = extractRequiredParameter(response, "openid", rawResponse).asText();
        var unionid = extractRequiredParameter(response, "unionid", rawResponse).asText();
        var token = new WechatToken(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse, openid, unionid);
        return token;
    }
}
