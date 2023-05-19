package org.pac4j.saml.exceptions;

import java.io.Serial;

/**
 * This is {@link SAMLAuthnSessionCriteriaException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLAuthnSessionCriteriaException extends SAMLException {
    @Serial
    private static final long serialVersionUID = 8635812340829541343L;

    /**
     * <p>Constructor for SAMLAuthnSessionCriteriaException.</p>
     *
     * @param message a {@link String} object
     */
    public SAMLAuthnSessionCriteriaException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLAuthnSessionCriteriaException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public SAMLAuthnSessionCriteriaException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLAuthnSessionCriteriaException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public SAMLAuthnSessionCriteriaException(final String message, final Throwable t) {
        super(message, t);
    }
}
