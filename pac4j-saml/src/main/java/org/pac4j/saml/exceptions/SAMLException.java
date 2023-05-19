
package org.pac4j.saml.exceptions;

import org.pac4j.core.exception.TechnicalException;

import java.io.Serial;

/**
 * Root exception for SAML Client.
 *
 * @author Michael Remond
 * @since 1.5.0
 */
public class SAMLException extends TechnicalException {

    @Serial
    private static final long serialVersionUID = -2963580056603469743L;

    /**
     * <p>Constructor for SAMLException.</p>
     *
     * @param message a {@link String} object
     */
    public SAMLException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public SAMLException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public SAMLException(final String message, final Throwable t) {
        super(message, t);
    }
}
