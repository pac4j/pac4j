package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLAssertionAudienceException}.
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SAMLAssertionAudienceException extends SAMLException {
    private static final long serialVersionUID = -3632157637070077883L;

    public SAMLAssertionAudienceException(final String message) {
        super(message);
    }

    public SAMLAssertionAudienceException(final Throwable t) {
        super(t);
    }

    public SAMLAssertionAudienceException(final String message, final Throwable t) {
        super(message, t);
    }
}
