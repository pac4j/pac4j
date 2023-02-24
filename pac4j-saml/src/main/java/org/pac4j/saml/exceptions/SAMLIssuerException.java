package org.pac4j.saml.exceptions;

/**
 * This is {@link org.pac4j.saml.exceptions.SAMLIssuerException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLIssuerException extends SAMLException {
    private static final long serialVersionUID = 6973714579016063655L;

    /**
     * <p>Constructor for SAMLIssuerException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public SAMLIssuerException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLIssuerException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLIssuerException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLIssuerException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLIssuerException(final String message, final Throwable t) {
        super(message, t);
    }
}
