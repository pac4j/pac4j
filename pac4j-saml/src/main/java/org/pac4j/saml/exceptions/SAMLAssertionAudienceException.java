package org.pac4j.saml.exceptions;

import java.io.Serial;

/**
 * This is {@link SAMLAssertionAudienceException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLAssertionAudienceException extends SAMLException {
    @Serial
    private static final long serialVersionUID = -3632157637070077883L;

    /**
     * <p>Constructor for SAMLAssertionAudienceException.</p>
     *
     * @param message a {@link String} object
     */
    public SAMLAssertionAudienceException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLAssertionAudienceException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public SAMLAssertionAudienceException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLAssertionAudienceException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public SAMLAssertionAudienceException(final String message, final Throwable t) {
        super(message, t);
    }
}
