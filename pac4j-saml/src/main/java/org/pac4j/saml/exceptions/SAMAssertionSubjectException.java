package org.pac4j.saml.exceptions;

import java.io.Serial;

/**
 * This is {@link SAMAssertionSubjectException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMAssertionSubjectException extends SAMLException {
    @Serial
    private static final long serialVersionUID = 3239759908829403730L;

    /**
     * <p>Constructor for SAMAssertionSubjectException.</p>
     *
     * @param message a {@link String} object
     */
    public SAMAssertionSubjectException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMAssertionSubjectException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public SAMAssertionSubjectException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMAssertionSubjectException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public SAMAssertionSubjectException(final String message, final Throwable t) {
        super(message, t);
    }
}
