package org.pac4j.oidc.exceptions;

/**
 * Exception indicating a mismatch between the metadata issuer and the response issuer.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcIssuerMismatchException extends OidcException {

    /**
     * <p>Constructor for OidcIssuerMismatchException.</p>
     *
     * @param message a {@link String} object
     */
    public OidcIssuerMismatchException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for OidcIssuerMismatchException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public OidcIssuerMismatchException(Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for OidcIssuerMismatchException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public OidcIssuerMismatchException(String message, Throwable t) {
        super(message, t);
    }
}
