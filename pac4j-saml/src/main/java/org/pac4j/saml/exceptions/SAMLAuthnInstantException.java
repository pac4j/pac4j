package org.pac4j.saml.exceptions;

import java.io.Serial;

/**
 * This is {@link SAMLAuthnInstantException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLAuthnInstantException extends SAMLException {
    @Serial
    private static final long serialVersionUID = 8085515962141416379L;

    /**
     * <p>Constructor for SAMLAuthnInstantException.</p>
     *
     * @param message a {@link String} object
     */
    public SAMLAuthnInstantException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLAuthnInstantException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public SAMLAuthnInstantException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLAuthnInstantException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public SAMLAuthnInstantException(final String message, final Throwable t) {
        super(message, t);
    }
}
