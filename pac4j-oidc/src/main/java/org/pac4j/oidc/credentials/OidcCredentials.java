package org.pac4j.oidc.credentials;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.minidev.json.JSONObject;
import org.pac4j.core.credentials.Credentials;

import java.io.Serial;
import java.util.Map;

/**
 * Credentials containing the authorization code sent by the OpenID Connect server.
 *
 * @author Michael Remond
 * @author Jerome Leleu
 * @since 1.7.0
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class OidcCredentials extends Credentials {

    @Serial
    private static final long serialVersionUID = 6772331801527223938L;

    @EqualsAndHashCode.Include
    private String code;

    private Map<String, ?> accessToken;

    private Map<String, ?> refreshToken;

    private String idToken;

    @JsonIgnore
    public AuthorizationCode toAuthorizationCode() {
        return code != null ? new AuthorizationCode(this.code) : null;
    }

    @JsonIgnore
    public AccessToken toAccessToken() {
        try {
            return accessToken != null ? AccessToken.parse(new JSONObject(accessToken)) : null;
        } catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @JsonIgnore
    public RefreshToken toRefreshToken() {
        try {
            return refreshToken != null ? RefreshToken.parse(new JSONObject(refreshToken)) : null;
        } catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @JsonIgnore
    public JWT toIdToken() {
        try {
            return idToken != null ? JWTParser.parse(this.idToken) : null;
        } catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
