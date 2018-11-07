package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLAuthnSessionCriteriaException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLAuthnSessionCriteriaException extends SAMLException {
    private static final long serialVersionUID = 8635812340829541343L;

    public SAMLAuthnSessionCriteriaException(final String message) {
        super(message);
    }

    public SAMLAuthnSessionCriteriaException(final Throwable t) {
        super(t);
    }

    public SAMLAuthnSessionCriteriaException(final String message, final Throwable t) {
        super(message, t);
    }
}
