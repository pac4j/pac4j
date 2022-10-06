package org.pac4j.saml.exceptions;

/**
 * This is {@link SAMLReplayException}.
 *
 * @since 3.8.0
 */
public class SAMLReplayException extends SAMLException {
    private static final long serialVersionUID = 6973714579016063655L;

    public SAMLReplayException(final String message) {
        super(message);
    }

    public SAMLReplayException(final Throwable t) {
        super(t);
    }

    public SAMLReplayException(final String message, final Throwable t) {
        super(message, t);
    }
}
