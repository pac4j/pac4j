package org.pac4j.core.profile.jwt;

/**
 * JWT claims: https://tools.ietf.org/html/rfc7519#page-9
 *
 * @author Jerome Leleu
 * @since 1.8.3
 */
public interface JwtClaims {

    /** Constant <code>ISSUER="iss"</code> */
    String ISSUER          = "iss";

    /** Constant <code>SUBJECT="sub"</code> */
    String SUBJECT         = "sub";

    /** Constant <code>EXPIRATION_TIME="exp"</code> */
    String EXPIRATION_TIME = "exp";

    /** Constant <code>NOT_BEFORE="nbf"</code> */
    String NOT_BEFORE      = "nbf";

    /** Constant <code>ISSUED_AT="iat"</code> */
    String ISSUED_AT       = "iat";

    /** Constant <code>JWT_ID="jti"</code> */
    String JWT_ID          = "jti";

    /** Constant <code>AUDIENCE="aud"</code> */
    String AUDIENCE        = "aud";
}
