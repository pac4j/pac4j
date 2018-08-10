package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLSubjectConfirmationException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLSubjectConfirmationException extends SAMLException {
    private static final long serialVersionUID = 8315215726586712166L;

    public SAMLSubjectConfirmationException(final String message) {
        super(message);
    }

    public SAMLSubjectConfirmationException(final Throwable t) {
        super(t);
    }

    public SAMLSubjectConfirmationException(final String message, final Throwable t) {
        super(message, t);
    }
}
