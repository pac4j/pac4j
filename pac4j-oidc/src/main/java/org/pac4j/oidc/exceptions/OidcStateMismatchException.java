package org.pac4j.oidc.exceptions;

/**
 * Exception indicating a mismatch between the session OIDC state and the value from the callback
 * state parameter.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcStateMismatchException extends OidcException {

    /**
     * <p>Constructor for OidcStateMismatchException.</p>
     *
     * @param message a {@link String} object
     */
    public OidcStateMismatchException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for OidcStateMismatchException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public OidcStateMismatchException(Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for OidcStateMismatchException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public OidcStateMismatchException(String message, Throwable t) {
        super(message, t);
    }
}
