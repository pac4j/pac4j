package org.pac4j.saml.exceptions;

import java.io.Serial;

/**
 * This is {@link SAMLAssertionConditionException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLAssertionConditionException extends SAMLException {
    @Serial
    private static final long serialVersionUID = -5853248740778879997L;

    /**
     * <p>Constructor for SAMLAssertionConditionException.</p>
     *
     * @param message a {@link String} object
     */
    public SAMLAssertionConditionException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLAssertionConditionException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public SAMLAssertionConditionException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLAssertionConditionException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public SAMLAssertionConditionException(final String message, final Throwable t) {
        super(message, t);
    }
}
