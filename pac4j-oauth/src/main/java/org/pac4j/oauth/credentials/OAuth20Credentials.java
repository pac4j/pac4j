package org.pac4j.oauth.credentials;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import org.pac4j.core.credentials.Credentials;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents an OAuth credentials for OAuth 2.0 an authorization code.
 *
 * @author zhangzhenli
 * @since 1.9.0
 */
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
public class OAuth20Credentials extends Credentials {

    @Serial
    private static final long serialVersionUID = -1370874913317625788L;

    @Setter
    @Getter
    private String code;

    @Setter
    private OAuth20AccessToken accessToken;

    /**
     * For OAuth2 Authorization Code Flow.
     *
     * @param code       the authorization code
     */
    public OAuth20Credentials(final String code) {
        this.code = code;
    }

    @JsonIgnore
    public OAuth2AccessToken toAccessToken() {
        return new OAuth2AccessToken(accessToken.getAccessToken(),
            accessToken.getTokenType(), accessToken.getExpiresIn(),
            accessToken.getRefreshToken(), accessToken.getScope(),
            accessToken.getRawResponse());
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @With
    public static class OAuth20AccessToken implements Serializable {
        @Serial
        private static final long serialVersionUID = -1370874913317625788L;

        private String accessToken;
        private String tokenType;
        private Integer expiresIn;
        private String refreshToken;
        private String scope;
        private String rawResponse;

        public static OAuth20AccessToken from(final OAuth2AccessToken accessToken) {
            return new OAuth20AccessToken(accessToken.getAccessToken(),
                accessToken.getTokenType(), accessToken.getExpiresIn(),
                accessToken.getRefreshToken(), accessToken.getScope(),
                accessToken.getRawResponse());
        }
    }
}
