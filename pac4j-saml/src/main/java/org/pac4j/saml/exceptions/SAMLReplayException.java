package org.pac4j.saml.exceptions;

/**
 * This is {@link org.pac4j.saml.exceptions.SAMLReplayException}.
 *
 * @since 3.8.0
 * @author bidou
 */
public class SAMLReplayException extends SAMLException {
    private static final long serialVersionUID = 6973714579016063655L;

    /**
     * <p>Constructor for SAMLReplayException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public SAMLReplayException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SAMLReplayException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLReplayException(final Throwable t) {
        super(t);
    }

    /**
     * <p>Constructor for SAMLReplayException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param t a {@link java.lang.Throwable} object
     */
    public SAMLReplayException(final String message, final Throwable t) {
        super(message, t);
    }
}
