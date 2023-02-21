package org.pac4j.oidc.exceptions;

/**
 * Exception indicating a mismatch between the session OIDC state and the value from the callback
 * state parameter.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcStateMismatchException extends OidcException {

    public OidcStateMismatchException(String message) {
        super(message);
    }

    public OidcStateMismatchException(Throwable t) {
        super(t);
    }

    public OidcStateMismatchException(String message, Throwable t) {
        super(message, t);
    }
}
