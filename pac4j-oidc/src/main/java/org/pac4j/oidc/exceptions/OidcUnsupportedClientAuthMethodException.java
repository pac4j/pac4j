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

    /**
     * <p>Constructor for OidcUnsupportedClientAuthMethodException.</p>
     *
     * @param message a {@link String} object
     */
    public OidcUnsupportedClientAuthMethodException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for OidcUnsupportedClientAuthMethodException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public OidcUnsupportedClientAuthMethodException(Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for OidcUnsupportedClientAuthMethodException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public OidcUnsupportedClientAuthMethodException(String message, Throwable t) {
        super(message, t);
    }
}
