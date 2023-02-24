package org.pac4j.saml.exceptions;

/**
 * This is {@link org.pac4j.saml.exceptions.SAMLAuthnInstantException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLAuthnInstantException extends SAMLException {
    private static final long serialVersionUID = 8085515962141416379L;

    /**
     * <p>Constructor for SAMLAuthnInstantException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public SAMLAuthnInstantException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLAuthnInstantException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLAuthnInstantException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLAuthnInstantException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLAuthnInstantException(final String message, final Throwable t) {
        super(message, t);
    }
}
