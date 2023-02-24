package org.pac4j.saml.exceptions;

/**
 * This is {@link org.pac4j.saml.exceptions.SAMAssertionSubjectException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMAssertionSubjectException extends SAMLException {
    private static final long serialVersionUID = 3239759908829403730L;

    /**
     * <p>Constructor for SAMAssertionSubjectException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public SAMAssertionSubjectException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMAssertionSubjectException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMAssertionSubjectException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMAssertionSubjectException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMAssertionSubjectException(final String message, final Throwable t) {
        super(message, t);
    }
}
