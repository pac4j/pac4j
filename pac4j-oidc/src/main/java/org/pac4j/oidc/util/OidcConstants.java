package org.pac4j.oidc.util;

/**
 * OpenID Connect constants.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
public interface OidcConstants {

    /** The parameter carrying the client secret, in the client_secret_post authentication (OpenID Connect Core 1.0, section 9). */
    String CLIENT_SECRET = "client_secret";

    /** The parameter carrying the client assertion, in the client_secret_jwt and private_key_jwt authentications
     * (OpenID Connect Core 1.0, section 9). */
    String CLIENT_ASSERTION = "client_assertion";
}
