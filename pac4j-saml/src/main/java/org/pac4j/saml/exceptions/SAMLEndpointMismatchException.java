package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLEndpointMismatchException}.
 *
 * @author Misagh Moayyed
 */
public class SAMLEndpointMismatchException extends SAMLException {
    public SAMLEndpointMismatchException(final String message) {
        super(message);
    }

    public SAMLEndpointMismatchException(final Throwable t) {
        super(t);
    }

    public SAMLEndpointMismatchException(final String message, final Throwable t) {
        super(message, t);
    }
}
