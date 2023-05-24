package org.pac4j.saml.exceptions;

import java.io.Serial;

/**
 * This is {@link SAMLSubjectConfirmationException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLSubjectConfirmationException extends SAMLException {
    @Serial
    private static final long serialVersionUID = 8315215726586712166L;

    /**
     * <p>Constructor for SAMLSubjectConfirmationException.</p>
     *
     * @param message a {@link String} object
     */
    public SAMLSubjectConfirmationException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLSubjectConfirmationException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public SAMLSubjectConfirmationException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLSubjectConfirmationException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public SAMLSubjectConfirmationException(final String message, final Throwable t) {
        super(message, t);
    }
}
