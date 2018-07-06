package org.pac4j.scribe.extractors;

import java.util.regex.Pattern;

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

    public static final Pattern OPENID_REGEX = Pattern.compile("\"openid\"\\s*:\\s*\"(\\S*?)\"");
    public static final Pattern UNION_ID_REGEX = Pattern.compile("\"unionid\"\\s*:\\s*\"(\\S*?)\"");

    protected WechatJsonExtractor() {
    }

    private static class InstanceHolder {

        private static final WechatJsonExtractor INSTANCE = new WechatJsonExtractor();
    }

    public static WechatJsonExtractor instance() {
        return WechatJsonExtractor.InstanceHolder.INSTANCE;
    }

    @Override
    protected OAuth2AccessToken createToken(String accessToken, String tokenType, Integer expiresIn,
                                            String refreshToken, String scope, String response) {
        String openid = extractParameter(response, OPENID_REGEX, true);
        String unionid = extractParameter(response, UNION_ID_REGEX, false);
        WechatToken token = new WechatToken(accessToken, tokenType, expiresIn, refreshToken, scope, response, openid, unionid);
        return token;
    }
}
