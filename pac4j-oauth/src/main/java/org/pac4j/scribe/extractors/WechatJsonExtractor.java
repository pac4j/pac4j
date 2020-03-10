package org.pac4j.scribe.extractors;

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.scribe.model.WechatToken;

import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;

/**
 * This class represents a specific JSON extractor for Wechat Connect using OAuth protocol version 2.
 * It could be part of the Scribe library.
 * <p>More info at: <a href=
 * "https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316505&token=&lang=zh_CN">
 * WeChat login development guide</a></p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WechatJsonExtractor extends OAuth2AccessTokenJsonExtractor {

    protected WechatJsonExtractor() {
    }

    private static class InstanceHolder {

        private static final WechatJsonExtractor INSTANCE = new WechatJsonExtractor();
    }

    public static WechatJsonExtractor instance() {
        return WechatJsonExtractor.InstanceHolder.INSTANCE;
    }

    @Override
    protected OAuth2AccessToken createToken(final String accessToken, final String tokenType,
                                            final Integer expiresIn, final String refreshToken, final String scope,
                                            final JsonNode response, final String rawResponse) {
        final String openid = extractRequiredParameter(response, "openid", rawResponse).asText();
        final String unionid = extractRequiredParameter(response, "unionid", rawResponse).asText();
        final WechatToken token = new WechatToken(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse, openid, unionid);
        return token;
    }
}
