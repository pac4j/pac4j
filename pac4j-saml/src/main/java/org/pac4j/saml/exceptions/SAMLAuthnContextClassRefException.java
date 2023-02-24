package org.pac4j.saml.exceptions;

/**
 * This is {@link org.pac4j.saml.exceptions.SAMLAuthnContextClassRefException}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class SAMLAuthnContextClassRefException extends SAMLException {
    private static final long serialVersionUID = 8635812340829541343L;

    /**
     * <p>Constructor for SAMLAuthnContextClassRefException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public SAMLAuthnContextClassRefException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLAuthnContextClassRefException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLAuthnContextClassRefException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLAuthnContextClassRefException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLAuthnContextClassRefException(final String message, final Throwable t) {
        super(message, t);
    }
}
