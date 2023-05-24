package org.pac4j.oidc.exceptions;

/**
 * Exception indicating that the OIDC state parameter is missing from the callback.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcMissingStateParameterException extends OidcException {

    /**
     * <p>Constructor for OidcMissingStateParameterException.</p>
     *
     * @param message a {@link String} object
     */
    public OidcMissingStateParameterException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for OidcMissingStateParameterException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public OidcMissingStateParameterException(Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for OidcMissingStateParameterException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public OidcMissingStateParameterException(String message, Throwable t) {
        super(message, t);
    }
}
