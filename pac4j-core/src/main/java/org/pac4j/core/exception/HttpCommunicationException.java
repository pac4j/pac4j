package org.pac4j.core.exception;

import java.io.Serial;

/**
 * This class represents an exception which can happen during HTTP communication (with status code and message body).
 *
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class HttpCommunicationException extends CommunicationException {

    @Serial
    private static final long serialVersionUID = -7972641539531738263L;

    private final int code;

    private final String body;

    /**
     * <p>Constructor for HttpCommunicationException.</p>
     *
     * @param code a int
     * @param body a {@link String} object
     */
    public HttpCommunicationException(final int code, final String body) {
        super("Failed to retrieve data / failed code : " + code + " and body : " + body);
        this.code = code;
        this.body = body;
    }

    /**
     * <p>Constructor for HttpCommunicationException.</p>
     *
     * @param t a {@link Throwable} object
     */
    public HttpCommunicationException(final Throwable t) {
        super(t);
        this.code = 0;
        this.body = null;
    }

    /**
     * <p>Constructor for HttpCommunicationException.</p>
     *
     * @param message a {@link String} object
     */
    public HttpCommunicationException(final String message) {
        super(message);
        this.code = 0;
        this.body = null;
    }

    /**
     * <p>Getter for the field <code>code</code>.</p>
     *
     * @return a int
     */
    public int getCode() {
        return this.code;
    }

    /**
     * <p>Getter for the field <code>body</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getBody() {
        return this.body;
    }
}
