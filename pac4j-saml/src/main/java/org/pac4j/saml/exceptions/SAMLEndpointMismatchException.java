package org.pac4j.saml.exceptions;

import java.io.Serial;

/**
 * This is {@link SAMLEndpointMismatchException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLEndpointMismatchException extends SAMLException {
    @Serial
    private static final long serialVersionUID = -1352860736771222912L;

    /**
     * <p>Constructor for SAMLEndpointMismatchException.</p>
     *
     * @param message a {@link String} object
     */
    public SAMLEndpointMismatchException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLEndpointMismatchException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public SAMLEndpointMismatchException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLEndpointMismatchException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public SAMLEndpointMismatchException(final String message, final Throwable t) {
        super(message, t);
    }
}
