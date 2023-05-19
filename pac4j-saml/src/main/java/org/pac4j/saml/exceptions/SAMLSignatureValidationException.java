package org.pac4j.saml.exceptions;

import java.io.Serial;

/**
 * This is {@link SAMLSignatureValidationException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLSignatureValidationException extends SAMLException {
    @Serial
    private static final long serialVersionUID = 7269870694809012877L;

    /**
     * <p>Constructor for SAMLSignatureValidationException.</p>
     *
     * @param message a {@link String} object
     */
    public SAMLSignatureValidationException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLSignatureValidationException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public SAMLSignatureValidationException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLSignatureValidationException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public SAMLSignatureValidationException(final String message, final Throwable t) {
        super(message, t);
    }
}
