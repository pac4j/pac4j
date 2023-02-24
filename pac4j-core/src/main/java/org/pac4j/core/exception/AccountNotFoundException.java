package org.pac4j.core.exception;

/**
 * Exception when an account is not found.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class AccountNotFoundException extends CredentialsException {

    private static final long serialVersionUID = -2405351263139588633L;

    /**
     * <p>Constructor for AccountNotFoundException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public AccountNotFoundException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for AccountNotFoundException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public AccountNotFoundException(final Throwable t) {
        super(t);
    }
}
