package org.pac4j.core.exception;

/**
 * This class represents the root technical exception for the library.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class TechnicalException extends RuntimeException {
    
    private static final long serialVersionUID = 536639932593211210L;
    
    public TechnicalException(final String message) {
        super(message);
    }
    
    public TechnicalException(final Throwable t) {
        super(t);
    }
    
    public TechnicalException(final String message, final Throwable t) {
        super(message, t);
    }
}
