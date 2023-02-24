package org.pac4j.saml.exceptions;

/**
 * This is {@link org.pac4j.saml.exceptions.SAMLInResponseToMismatchException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLInResponseToMismatchException extends SAMLException {
    private static final long serialVersionUID = 7977187629415365600L;

    /**
     * <p>Constructor for SAMLInResponseToMismatchException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public SAMLInResponseToMismatchException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLInResponseToMismatchException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLInResponseToMismatchException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLInResponseToMismatchException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLInResponseToMismatchException(final String message, final Throwable t) {
        super(message, t);
    }
}
