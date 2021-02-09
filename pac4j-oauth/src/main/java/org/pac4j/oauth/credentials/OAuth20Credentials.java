package org.pac4j.oauth.credentials;

import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;

/**
 * This class represents an OAuth credentials for OAuth 2.0 an authorization code.
 *
 * @author zhangzhenli
 * @since 1.9.0
 */
public class OAuth20Credentials extends Credentials {

    private static final long serialVersionUID = -1370874913317625788L;
    private String code;

    private OAuth2AccessToken accessToken;

    /**
     * For OAuth2 Authorization Code Flow.
     *
     * @param code       the authorization code
     */
    public OAuth20Credentials(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var that = (OAuth20Credentials) o;

        return code != null ? code.equals(that.code) : that.code == null;

    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final OAuth2AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(),
                "code", code,
                "accessToken", accessToken);
    }
}
