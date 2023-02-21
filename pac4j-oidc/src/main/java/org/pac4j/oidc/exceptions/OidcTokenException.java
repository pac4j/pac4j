package org.pac4j.oidc.exceptions;

/**
 * Exception indicating problems related to OIDC token exchange.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcTokenException extends OidcException {

    public OidcTokenException(String message) {
        super(message);
    }

    public OidcTokenException(Throwable t) {
        super(t);
    }

    public OidcTokenException(String message, Throwable t) {
        super(message, t);
    }
}
