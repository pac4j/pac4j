package org.pac4j.scribe.model;

import com.github.scribejava.core.model.OAuth2AccessToken;

import java.io.Serial;

/**
 * Weibo token extra.
 * <p>More info at: <a href="http://open.weibo.com/wiki/Oauth2/access_token">Oauth2/access token</a></p>
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WeiboToken extends OAuth2AccessToken {

    @Serial
    private static final long serialVersionUID = 1489916603771001585L;
    private String uid;

    /**
     * <p>Constructor for WeiboToken.</p>
     *
     * @param accessToken a {@link OAuth2AccessToken} object
     * @param uid a {@link String} object
     */
    public WeiboToken(OAuth2AccessToken accessToken, String uid) {
        super(accessToken.getAccessToken(), accessToken.getTokenType(), accessToken.getExpiresIn(),
            accessToken.getRefreshToken(), accessToken.getScope(),
            accessToken.getRawResponse());
        this.uid = uid;
    }


    /**
     * <p>Getter for the field <code>uid</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getUid() {
        return uid;
    }

    /**
     * <p>Setter for the field <code>uid</code>.</p>
     *
     * @param uid a {@link String} object
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeiboToken that)) return false;
        if (!super.equals(o)) return false;

        return uid != null ? uid.equals(that.uid) : that.uid == null;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        var result = super.hashCode();
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        return result;
    }
}
