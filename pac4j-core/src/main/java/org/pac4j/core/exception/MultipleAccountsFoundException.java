package org.pac4j.core.exception;

import java.io.Serial;

/**
 * Exception when multiple accounts are found.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class MultipleAccountsFoundException extends TechnicalException {

    @Serial
    private static final long serialVersionUID = 1430582289490541876L;

    /**
     * <p>Constructor for MultipleAccountsFoundException.</p>
     *
     * @param message a {@link String} object
     */
    public MultipleAccountsFoundException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for MultipleAccountsFoundException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public MultipleAccountsFoundException(final Throwable t) {
        super(t);
    }
}
