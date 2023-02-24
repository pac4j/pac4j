package org.pac4j.saml.exceptions;

/**
 * This is {@link org.pac4j.saml.exceptions.SAMLSubjectConfirmationException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLSubjectConfirmationException extends SAMLException {
    private static final long serialVersionUID = 8315215726586712166L;

    /**
     * <p>Constructor for SAMLSubjectConfirmationException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public SAMLSubjectConfirmationException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLSubjectConfirmationException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLSubjectConfirmationException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLSubjectConfirmationException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLSubjectConfirmationException(final String message, final Throwable t) {
        super(message, t);
    }
}
