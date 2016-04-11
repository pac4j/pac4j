package org.pac4j.core.exception;

/**
 * Exception when multiple accounts are found.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class MultipleAccountsFoundException extends CredentialsException {

    private static final long serialVersionUID = 1430582289490541876L;

    public MultipleAccountsFoundException(final String message) {
        super(message);
    }

    public MultipleAccountsFoundException(final Throwable t) {
        super(t);
    }
}
