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

    /**
     * <p>Constructor for WechatToken.</p>
     *
     * @param accessToken a {@link java.lang.String} object
     * @param tokenType a {@link java.lang.String} object
     * @param expiresIn a {@link java.lang.Integer} object
     * @param refreshToken a {@link java.lang.String} object
     * @param scope a {@link java.lang.String} object
     * @param rawResponse a {@link java.lang.String} object
     * @param openid a {@link java.lang.String} object
     * @param unionid a {@link java.lang.String} object
     */
    public WechatToken(String accessToken, String tokenType, Integer expiresIn,
                       String refreshToken, String scope, String rawResponse,
                       String openid, String unionid) {
        super(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse);
        this.openid = openid;
        this.unionid = unionid;
    }
}
