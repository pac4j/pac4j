package org.pac4j.saml.exceptions;

/**
 * This is {@link org.pac4j.saml.exceptions.SAMLAuthnSessionCriteriaException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLAuthnSessionCriteriaException extends SAMLException {
    private static final long serialVersionUID = 8635812340829541343L;

    /**
     * <p>Constructor for SAMLAuthnSessionCriteriaException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public SAMLAuthnSessionCriteriaException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLAuthnSessionCriteriaException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLAuthnSessionCriteriaException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLAuthnSessionCriteriaException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLAuthnSessionCriteriaException(final String message, final Throwable t) {
        super(message, t);
    }
}
