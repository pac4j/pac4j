package org.pac4j.scribe.model;

import com.github.scribejava.core.model.OAuth2AccessToken;

/**
 * Weibo token extra.
 * <p>More info at: <a href="http://open.weibo.com/wiki/Oauth2/access_token">Oauth2/access token</a></p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WeiboToken extends OAuth2AccessToken {

    private static final long serialVersionUID = 1489916603771001585L;
    private String uid;

    public WeiboToken(OAuth2AccessToken accessToken, String uid) {
        super(accessToken.getAccessToken(), accessToken.getTokenType(), accessToken.getExpiresIn(),
            accessToken.getRefreshToken(), accessToken.getScope(),
            accessToken.getRawResponse());
        this.uid = uid;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeiboToken)) return false;
        if (!super.equals(o)) return false;

        WeiboToken that = (WeiboToken) o;

        return uid != null ? uid.equals(that.uid) : that.uid == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        return result;
    }
}
