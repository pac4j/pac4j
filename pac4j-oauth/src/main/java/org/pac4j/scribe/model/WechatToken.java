package org.pac4j.scribe.model;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Wechat token extra.
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class WechatToken extends OAuth2AccessToken {

    private static final long serialVersionUID = -4657457530761699382L;
    private String openid;
    private String unionid;

    public WechatToken(String accessToken, String tokenType, Integer expiresIn,
                       String refreshToken, String scope, String rawResponse,
                       String openid, String unionid) {
        super(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse);
        this.openid = openid;
        this.unionid = unionid;
    }
}
