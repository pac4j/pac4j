package org.pac4j.oauth.credentials;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.credentials.Credentials;

import java.io.Serial;

/**
 * This class represents an OAuth credentials for OAuth 2.0 an authorization code.
 *
 * @author zhangzhenli
 * @since 1.9.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class OAuth20Credentials extends Credentials {

    @Serial
    private static final long serialVersionUID = -1370874913317625788L;
    private String code;

    @Setter
    private OAuth2AccessToken accessToken;

    /**
     * For OAuth2 Authorization Code Flow.
     *
     * @param code       the authorization code
     */
    public OAuth20Credentials(String code) {
        this.code = code;
    }
}
