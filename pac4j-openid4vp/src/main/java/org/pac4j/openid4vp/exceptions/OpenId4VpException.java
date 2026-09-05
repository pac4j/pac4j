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
}
