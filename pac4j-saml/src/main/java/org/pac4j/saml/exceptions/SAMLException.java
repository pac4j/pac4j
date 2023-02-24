
package org.pac4j.saml.exceptions;

import org.pac4j.core.exception.TechnicalException;

/**
 * Root exception for SAML Client.
 *
 * @author Michael Remond
 * @since 1.5.0
 */
public class SAMLException extends TechnicalException {

    private static final long serialVersionUID = -2963580056603469743L;

    /**
     * <p>Constructor for SAMLException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public SAMLException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLException(final String message, final Throwable t) {
        super(message, t);
    }
}
