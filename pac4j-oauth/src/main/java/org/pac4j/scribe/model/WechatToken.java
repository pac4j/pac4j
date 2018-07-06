package org.pac4j.scribe.model;

import org.pac4j.core.util.CommonHelper;

import com.github.scribejava.core.model.OAuth2AccessToken;

/**
 * Wechat token extra.
 * <p>More info at: <a href=
 * "https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316518&token=&lang=zh_CN">
 * access_token</a></p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WechatToken extends OAuth2AccessToken {

    private String openid;
    private String unionid;

    public WechatToken(String accessToken, String tokenType, Integer expiresIn,
                       String refreshToken, String scope, String rawResponse,
                       String openid, String unionid) {
        super(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse);
        this.openid = openid;
        this.unionid = unionid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WechatToken that = (WechatToken) o;

        if (openid != null ? !openid.equals(that.openid) : that.openid != null) return false;
        return unionid != null ? unionid.equals(that.unionid) : that.unionid == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (openid != null ? openid.hashCode() : 0);
        result = 31 * result + (unionid != null ? unionid.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(WechatToken.class, "accessToken", getAccessToken(),
            "openid", openid, "unionid", unionid);
    }
}
