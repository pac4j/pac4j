package org.pac4j.saml.exceptions;

import java.io.Serial;

/**
 * This is {@link SAMLSignatureRequiredException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLSignatureRequiredException extends SAMLException {
    @Serial
    private static final long serialVersionUID = 7054748433033124696L;

    /**
     * <p>Constructor for SAMLSignatureRequiredException.</p>
     *
     * @param message a {@link String} object
     */
    public SAMLSignatureRequiredException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLSignatureRequiredException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public SAMLSignatureRequiredException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLSignatureRequiredException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public SAMLSignatureRequiredException(final String message, final Throwable t) {
        super(message, t);
    }
}
