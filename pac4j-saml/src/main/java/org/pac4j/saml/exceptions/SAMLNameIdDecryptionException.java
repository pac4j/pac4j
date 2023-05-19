package org.pac4j.saml.exceptions;

import java.io.Serial;

/**
 * This is {@link SAMLNameIdDecryptionException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLNameIdDecryptionException extends SAMLException {
    @Serial
    private static final long serialVersionUID = 1452441332362175576L;

    /**
     * <p>Constructor for SAMLNameIdDecryptionException.</p>
     *
     * @param message a {@link String} object
     */
    public SAMLNameIdDecryptionException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLNameIdDecryptionException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public SAMLNameIdDecryptionException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLNameIdDecryptionException.</p>
     *
     * @param message a {@link String} object
     * @param t a {@link Throwable} object
     */
    public SAMLNameIdDecryptionException(final String message, final Throwable t) {
        super(message, t);
    }
}
