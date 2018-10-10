package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLInResponseToMismatchException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLInResponseToMismatchException extends SAMLException {
    private static final long serialVersionUID = 7977187629415365600L;

    public SAMLInResponseToMismatchException(final String message) {
        super(message);
    }

    public SAMLInResponseToMismatchException(final Throwable t) {
        super(t);
    }

    public SAMLInResponseToMismatchException(final String message, final Throwable t) {
        super(message, t);
    }
}
