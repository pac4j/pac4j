package org.pac4j.oidc.credentials;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.credentials.Credentials;

import java.io.Serial;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OidcCredentials extends Credentials {

    @Serial
    private static final long serialVersionUID = 6772331801527223938L;

    @EqualsAndHashCode.Include
    private AuthorizationCode code;
    private AccessToken accessToken;
    private RefreshToken refreshToken;
    private JWT idToken;
}
