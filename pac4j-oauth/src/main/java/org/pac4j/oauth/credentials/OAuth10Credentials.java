package org.pac4j.oauth.credentials;

import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuth1Token;
import org.pac4j.core.util.CommonHelper;

/**
 * This class represents an OAuth credentials for OAuth 1.0 a request token, a token and a verifier.
 *
 * @author zhangzhenli
 * @since 1.9.0
 */
public class OAuth10Credentials extends OAuthCredentials {

    private OAuth1RequestToken requestToken;

    private String token;

    private String verifier;

    private OAuth1Token accessToken;

    public OAuth10Credentials(OAuth1RequestToken requestToken, String token, String verifier, String clientName) {
        super(clientName);
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

    public OAuth1Token getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final OAuth1Token accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OAuth10Credentials that = (OAuth10Credentials) o;

        if (requestToken != null ? !requestToken.equals(that.requestToken) : that.requestToken != null) return false;
        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        return verifier != null ? verifier.equals(that.verifier) : that.verifier == null;

    }

    @Override
    public int hashCode() {
        int result = requestToken != null ? requestToken.hashCode() : 0;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (verifier != null ? verifier.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(),
                "requestToken", requestToken,
                "token", token,
                "verifier'", verifier,
                "clientName", getClientName(),
                "accessToken", accessToken);
    }
}
