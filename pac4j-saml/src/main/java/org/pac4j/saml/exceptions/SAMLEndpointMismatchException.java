package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLEndpointMismatchException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLEndpointMismatchException extends SAMLException {
    private static final long serialVersionUID = -1352860736771222912L;

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
