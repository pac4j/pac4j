package org.pac4j.saml.exceptions;

/**
 * This is {@link org.pac4j.saml.exceptions.SAMLAssertionAudienceException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLAssertionAudienceException extends SAMLException {
    private static final long serialVersionUID = -3632157637070077883L;

    /**
     * <p>Constructor for SAMLAssertionAudienceException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public SAMLAssertionAudienceException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLAssertionAudienceException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLAssertionAudienceException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLAssertionAudienceException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLAssertionAudienceException(final String message, final Throwable t) {
        super(message, t);
    }
}
