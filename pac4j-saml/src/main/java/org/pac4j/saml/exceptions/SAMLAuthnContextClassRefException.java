package org.pac4j.saml.exceptions;

import java.io.Serial;

/**
 * This is {@link SAMLAuthnContextClassRefException}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class SAMLAuthnContextClassRefException extends SAMLException {
    @Serial
    private static final long serialVersionUID = 8635812340829541343L;

    /**
     * <p>Constructor for SAMLAuthnContextClassRefException.</p>
     *
     * @param message a {@link String} object
     */
    public SAMLAuthnContextClassRefException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLAuthnContextClassRefException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public SAMLAuthnContextClassRefException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLAuthnContextClassRefException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public SAMLAuthnContextClassRefException(final String message, final Throwable t) {
        super(message, t);
    }
}
