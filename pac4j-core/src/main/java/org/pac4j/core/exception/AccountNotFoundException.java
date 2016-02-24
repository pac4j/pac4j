package org.pac4j.core.exception;

/**
 * Exception when an account is not found.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class AccountNotFoundException extends CredentialsException {

    private static final long serialVersionUID = -2405351263139588633L;

    public AccountNotFoundException(final String message) {
        super(message);
    }

    public AccountNotFoundException(final Throwable t) {
        super(t);
    }
}
