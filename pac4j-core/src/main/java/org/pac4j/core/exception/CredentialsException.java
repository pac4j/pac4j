package org.pac4j.core.exception;

/**
 * This class represents an exception occuring during credentials retrieval.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CredentialsException extends TechnicalException {
    
    private static final long serialVersionUID = 8188990220217650629L;
    
    public CredentialsException(final String message) {
        super(message);
    }
    
    public CredentialsException(final Throwable t) {
        super(t);
    }

    public CredentialsException(String message, Throwable t) {
        super(message, t);
    }
}
