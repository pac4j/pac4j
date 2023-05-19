package org.pac4j.oidc.exceptions;

/**
 * Exception indicating that the OIDC state is missing from the session.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcMissingSessionStateException extends OidcException {

    /**
     * <p>Constructor for OidcMissingSessionStateException.</p>
     *
     * @param message a {@link String} object
     */
    public OidcMissingSessionStateException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for OidcMissingSessionStateException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public OidcMissingSessionStateException(Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for OidcMissingSessionStateException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public OidcMissingSessionStateException(String message, Throwable t) {
        super(message, t);
    }
}
