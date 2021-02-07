package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLAuthnContextClassRefException}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class SAMLAuthnContextClassRefException extends SAMLException {
    private static final long serialVersionUID = 8635812340829541343L;

    public SAMLAuthnContextClassRefException(final String message) {
        super(message);
    }

    public SAMLAuthnContextClassRefException(final Throwable t) {
        super(t);
    }

    public SAMLAuthnContextClassRefException(final String message, final Throwable t) {
        super(message, t);
    }
}
