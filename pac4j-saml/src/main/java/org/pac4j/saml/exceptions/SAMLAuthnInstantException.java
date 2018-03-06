package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLAuthnInstantException}.
 *
 * @author Misagh Moayyed
 */
public class SAMLAuthnInstantException extends SAMLException {
    public SAMLAuthnInstantException(final String message) {
        super(message);
    }

    public SAMLAuthnInstantException(final Throwable t) {
        super(t);
    }

    public SAMLAuthnInstantException(final String message, final Throwable t) {
        super(message, t);
    }
}
