package org.pac4j.saml.exceptions;

/**
 * This is {@link org.pac4j.saml.exceptions.SAMLSignatureRequiredException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLSignatureRequiredException extends SAMLException {
    private static final long serialVersionUID = 7054748433033124696L;

    /**
     * <p>Constructor for SAMLSignatureRequiredException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public SAMLSignatureRequiredException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLSignatureRequiredException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLSignatureRequiredException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLSignatureRequiredException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLSignatureRequiredException(final String message, final Throwable t) {
        super(message, t);
    }
}
