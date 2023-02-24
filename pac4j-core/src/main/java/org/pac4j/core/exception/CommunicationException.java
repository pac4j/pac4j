package org.pac4j.core.exception;

/**
 * This class represents a communication exception.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CommunicationException extends TechnicalException {

    private static final long serialVersionUID = 3817212490339517957L;

    /**
     * <p>Constructor for CommunicationException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public CommunicationException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for CommunicationException.</p>
     *
     * @param t a {@link java.lang.Throwable} object
     */
    public CommunicationException(final Throwable t) {
        super(t);
    }
}
