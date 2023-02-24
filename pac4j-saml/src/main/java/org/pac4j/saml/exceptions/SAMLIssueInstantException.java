package org.pac4j.saml.exceptions;

/**
 * This is {@link org.pac4j.saml.exceptions.SAMLIssueInstantException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLIssueInstantException extends SAMLException {
    private static final long serialVersionUID = -3487208015746622248L;

    /**
     * <p>Constructor for SAMLIssueInstantException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public SAMLIssueInstantException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLIssueInstantException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLIssueInstantException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLIssueInstantException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLIssueInstantException(final String message, final Throwable t) {
        super(message, t);
    }
}
