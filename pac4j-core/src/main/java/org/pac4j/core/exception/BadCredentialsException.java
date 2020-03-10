package org.pac4j.core.exception;

/**
 * Exception for bad credentials.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class BadCredentialsException extends CredentialsException {

    private static final long serialVersionUID = 106849753775292065L;

    public BadCredentialsException(final String message) {
        super(message);
    }

    public BadCredentialsException(final Throwable t) {
        super(t);
    }

    public BadCredentialsException(final String message, final Throwable t) {
        super(message, t);
    }
}
