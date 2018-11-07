package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLSignatureRequiredException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLSignatureRequiredException extends SAMLException {
    public SAMLSignatureRequiredException(final String message) {
        super(message);
    }

    public SAMLSignatureRequiredException(final Throwable t) {
        super(t);
    }

    public SAMLSignatureRequiredException(final String message, final Throwable t) {
        super(message, t);
    }
}
