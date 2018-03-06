package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLAssertionConditionException}.
 *
 * @author Misagh Moayyed
 */
public class SAMLAssertionConditionException extends SAMLException {
    public SAMLAssertionConditionException(final String message) {
        super(message);
    }

    public SAMLAssertionConditionException(final Throwable t) {
        super(t);
    }

    public SAMLAssertionConditionException(final String message, final Throwable t) {
        super(message, t);
    }
}
