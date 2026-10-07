package org.pac4j.openid4vp.exceptions;

import org.pac4j.core.exception.TechnicalException;

import java.io.Serial;

/**
 * Exception dedicated to the OpenID4VP support.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
public class OpenId4VpException extends TechnicalException {

    @Serial
    private static final long serialVersionUID = -4283242761364272860L;

    /**
     * <p>Constructor for OpenId4VpException.</p>
     *
     * @param message a {@link String} object
     */
    public OpenId4VpException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for OpenId4VpException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public OpenId4VpException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for OpenId4VpException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public OpenId4VpException(final String message, final Throwable t) {
        super(message, t);
    }

    /**
     * <p>The reasons of a failure: its message, followed by the messages of its causes, a verifier wrapping the
     * failure of the library it relies on.</p>
     *
     * @param e the failure
     * @return the reasons
     */
    public static String reasons(final Throwable e) {
        final var reasons = new StringBuilder(String.valueOf(e.getMessage()));
        var cause = e.getCause();
        while (cause != null && cause != e) {
            reasons.append(" <- ").append(cause.getMessage());
            cause = cause.getCause() == cause ? null : cause.getCause();
        }
        return reasons.toString();
    }
}
