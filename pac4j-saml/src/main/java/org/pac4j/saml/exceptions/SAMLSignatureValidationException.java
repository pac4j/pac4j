package org.pac4j.saml.exceptions;

/**
 * This is {@link org.pac4j.saml.exceptions.SAMLSignatureValidationException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLSignatureValidationException extends SAMLException {
    private static final long serialVersionUID = 7269870694809012877L;

    /**
     * <p>Constructor for SAMLSignatureValidationException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public SAMLSignatureValidationException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLSignatureValidationException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLSignatureValidationException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLSignatureValidationException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLSignatureValidationException(final String message, final Throwable t) {
        super(message, t);
    }
}
