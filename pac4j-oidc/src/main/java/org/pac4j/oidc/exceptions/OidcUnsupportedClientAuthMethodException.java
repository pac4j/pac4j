package org.pac4j.oidc.exceptions;

/**
 * Exception indicating that the requested client auth method for the token exchange
 * is not supported.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcUnsupportedClientAuthMethodException extends
    OidcException {

    public OidcUnsupportedClientAuthMethodException(String message) {
        super(message);
    }

    public OidcUnsupportedClientAuthMethodException(Throwable t) {
        super(t);
    }

    public OidcUnsupportedClientAuthMethodException(String message, Throwable t) {
        super(message, t);
    }
}
