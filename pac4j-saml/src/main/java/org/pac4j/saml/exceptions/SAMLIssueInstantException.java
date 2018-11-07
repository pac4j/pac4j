package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLIssueInstantException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLIssueInstantException extends SAMLException {
    private static final long serialVersionUID = -3487208015746622248L;

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
