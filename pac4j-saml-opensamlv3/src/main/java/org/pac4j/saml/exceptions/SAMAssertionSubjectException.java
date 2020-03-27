package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMAssertionSubjectException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMAssertionSubjectException extends SAMLException {
    private static final long serialVersionUID = 3239759908829403730L;

    public SAMAssertionSubjectException(final String message) {
        super(message);
    }

    public SAMAssertionSubjectException(final Throwable t) {
        super(t);
    }

    public SAMAssertionSubjectException(final String message, final Throwable t) {
        super(message, t);
    }
}
