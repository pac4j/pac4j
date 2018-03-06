package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLNameIdDecryptionException}.
 *
 * @author Misagh Moayyed
 */
public class SAMLNameIdDecryptionException extends SAMLException {
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
