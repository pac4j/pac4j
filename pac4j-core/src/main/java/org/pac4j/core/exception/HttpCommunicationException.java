package org.pac4j.core.exception;

/**
 * This class represents an exception which can happen during HTTP communication (with status code and message body).
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class HttpCommunicationException extends CommunicationException {
    
    private static final long serialVersionUID = -7972641539531738263L;
    
    private final int code;
    
    private final String body;
    
    public HttpCommunicationException(final int code, final String body) {
        super("Failed to retrieve data / failed code : " + code + " and body : " + body);
        this.code = code;
        this.body = body;
    }
    
    public HttpCommunicationException(final Throwable t) {
        super(t);
        this.code = 0;
        this.body = null;
    }
    
    public HttpCommunicationException(final String message) {
        super(message);
        this.code = 0;
        this.body = null;
    }
    
    public int getCode() {
        return this.code;
    }
    
    public String getBody() {
        return this.body;
    }
}
