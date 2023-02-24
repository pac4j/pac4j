package org.pac4j.oidc.exceptions;

/**
 * Exception indicating problems related to OIDC token exchange.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcTokenException extends OidcException {

    /**
     * <p>Constructor for OidcTokenException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public OidcTokenException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for OidcTokenException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public OidcTokenException(Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for OidcTokenException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public OidcTokenException(String message, Throwable t) {
        super(message, t);
    }
}
