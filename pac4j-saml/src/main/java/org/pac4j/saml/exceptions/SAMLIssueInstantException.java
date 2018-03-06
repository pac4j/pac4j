package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLIssueInstantException}.
 *
 * @author Misagh Moayyed
 */
public class SAMLIssueInstantException extends SAMLException {
    public SAMLIssueInstantException(final String message) {
        super(message);
    }

    public SAMLIssueInstantException(final Throwable t) {
        super(t);
    }

    public SAMLIssueInstantException(final String message, final Throwable t) {
        super(message, t);
    }
}
