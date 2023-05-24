package org.pac4j.oidc.exceptions;

import org.pac4j.core.exception.TechnicalException;

/**
 * Root exception for OIDC client.
 *
 * @author Mathias Loesch
 * @since 6.0.0
 */
public class OidcException extends TechnicalException {

    /**
     * <p>Constructor for OidcException.</p>
     *
     * @param message a {@link String} object
     */
    public OidcException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for OidcException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public OidcException(Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for OidcException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public OidcException(String message, Throwable t) {
        super(message, t);
    }
}
