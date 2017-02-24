package org.pac4j.core.profile.jwt;

/**
 * JWT claims: https://tools.ietf.org/html/rfc7519#page-9
 *
 * @author Jerome Leleu
 * @since 1.8.3
 */
public interface JwtClaims {

    String ISSUER          = "iss";

    String SUBJECT         = "sub";

    String EXPIRATION_TIME = "exp";

    String NOT_BEFORE      = "nbf";

    String ISSUED_AT       = "iat";

    String JWT_ID          = "jti";

    String AUDIENCE        = "aud";
}
