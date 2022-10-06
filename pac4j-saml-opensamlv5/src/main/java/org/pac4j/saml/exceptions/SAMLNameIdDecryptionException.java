package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLNameIdDecryptionException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLNameIdDecryptionException extends SAMLException {
    private static final long serialVersionUID = 1452441332362175576L;

    public SAMLNameIdDecryptionException(final String message) {
        super(message);
    }

    public SAMLNameIdDecryptionException(final Throwable t) {
        super(t);
    }

    public SAMLNameIdDecryptionException(final String message, final Throwable t) {
        super(message, t);
    }
}
