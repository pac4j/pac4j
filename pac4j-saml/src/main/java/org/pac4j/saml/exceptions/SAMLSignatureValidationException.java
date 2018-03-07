package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLSignatureValidationException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLSignatureValidationException extends SAMLException {
    public SAMLSignatureValidationException(final String message) {
        super(message);
    }

    public SAMLSignatureValidationException(final Throwable t) {
        super(t);
    }

    public SAMLSignatureValidationException(final String message, final Throwable t) {
        super(message, t);
    }
}
