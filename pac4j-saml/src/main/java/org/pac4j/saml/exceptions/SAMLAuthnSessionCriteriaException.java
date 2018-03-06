package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLAuthnSessionCriteriaException}.
 *
 * @author Misagh Moayyed
 */
public class SAMLAuthnSessionCriteriaException extends SAMLException {
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
