package org.pac4j.oauth.credentials;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;

/**
 * This class represents an OAuth credentials for OAuth 1.0 a request token, a token and a verifier.
 *
 * @author zhangzhenli
 * @since 1.9.0
 */
public class OAuth10Credentials extends Credentials {

    private static final long serialVersionUID = -167715058574799025L;
    private OAuth1RequestToken requestToken;

    private String token;

    private String verifier;

    private OAuth1AccessToken accessToken;

    public OAuth10Credentials(OAuth1RequestToken requestToken, String token, String verifier) {
        this.requestToken = requestToken;
        this.token = token;
        this.verifier = verifier;
    }

    public OAuth1RequestToken getRequestToken() {
        return this.requestToken;
    }

    public String getToken() {
        return this.token;
    }

    public String getVerifier() {
        return this.verifier;
    }

    public OAuth1AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final OAuth1AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var that = (OAuth10Credentials) o;

        if (requestToken != null ? !requestToken.equals(that.requestToken) : that.requestToken != null) return false;
        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        return verifier != null ? verifier.equals(that.verifier) : that.verifier == null;

    }

    @Override
    public int hashCode() {
        var result = requestToken != null ? requestToken.hashCode() : 0;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (verifier != null ? verifier.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(),
                "requestToken", requestToken,
                "token", token,
                "verifier'", verifier,
                "accessToken", accessToken);
    }
}
