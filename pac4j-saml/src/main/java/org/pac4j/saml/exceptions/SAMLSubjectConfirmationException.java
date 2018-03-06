package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLSubjectConfirmationException}.
 *
 * @author Misagh Moayyed
 */
public class SAMLSubjectConfirmationException extends SAMLException {
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
