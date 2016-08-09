package org.pac4j.oidc.profile;

/**
 * JWT ID Token claims: http://openid.net/specs/openid-connect-core-1_0.html#IDToken
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public interface JwtIdTokenClaims {

    String ISSUER          = "iss";

    String SUBJECT         = "sub";

    String AUDIENCE        = "aud";

    String EXPIRATION_TIME = "exp";

    String ISSUED_AT       = "iat";

    String AUTH_TIME       = "auth_time";

    String NONCE           = "nonce";

    String ACR             = "acr";

    String AMR             = "amr";

    String AZP             = "azp";
}
