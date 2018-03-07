package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLInResponseToMismatchException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLInResponseToMismatchException extends SAMLException {
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
