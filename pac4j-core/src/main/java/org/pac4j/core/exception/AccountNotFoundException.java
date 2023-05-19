package org.pac4j.core.exception;

import java.io.Serial;

/**
 * Exception when an account is not found.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class AccountNotFoundException extends CredentialsException {

    @Serial
    private static final long serialVersionUID = -2405351263139588633L;

    /**
     * <p>Constructor for AccountNotFoundException.</p>
     *
     * @param message a {@link String} object
     */
    public AccountNotFoundException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for AccountNotFoundException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public AccountNotFoundException(final Throwable t) {
        super(t);
    }
}
