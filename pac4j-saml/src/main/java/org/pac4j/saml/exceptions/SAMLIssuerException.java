package org.pac4j.saml.exceptions;

import java.io.Serial;

/**
 * This is {@link SAMLIssuerException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLIssuerException extends SAMLException {
    @Serial
    private static final long serialVersionUID = 6973714579016063655L;

    /**
     * <p>Constructor for SAMLIssuerException.</p>
     *
     * @param message a {@link String} object
     */
    public SAMLIssuerException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLIssuerException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public SAMLIssuerException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLIssuerException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public SAMLIssuerException(final String message, final Throwable t) {
        super(message, t);
    }
}
