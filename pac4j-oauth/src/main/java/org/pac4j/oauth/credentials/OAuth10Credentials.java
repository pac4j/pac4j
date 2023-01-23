package org.pac4j.oauth.credentials;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.credentials.AuthenticationCredentials;

/**
 * This class represents an OAuth credentials for OAuth 1.0 a request token, a token and a verifier.
 *
 * @author zhangzhenli
 * @since 1.9.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class OAuth10Credentials extends AuthenticationCredentials {

    private static final long serialVersionUID = -167715058574799025L;
    private OAuth1RequestToken requestToken;

    private String token;

    private String verifier;

    @Setter
    private OAuth1AccessToken accessToken;

    public OAuth10Credentials(OAuth1RequestToken requestToken, String token, String verifier) {
        this.requestToken = requestToken;
        this.token = token;
        this.verifier = verifier;
    }
}
