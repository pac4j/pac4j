package org.pac4j.oauth.exception;

import org.pac4j.core.exception.CredentialsException;

import java.util.*;

/**
 * This class represents an exception occurring during OAuth credentials retrieval.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class OAuthCredentialsException extends CredentialsException {
    
    private static final long serialVersionUID = -3540979749535811079L;
    
    public static final String ERROR = "error";
    
    public static final String ERROR_REASON = "error_reason";
    
    public static final String ERROR_DESCRIPTION = "error_description";
    
    private static final String ERROR_URI = "error_uri";
    
    public static final List<String> ERROR_NAMES = Collections.unmodifiableList(Arrays.asList(
            new String[] {ERROR, ERROR_REASON, ERROR_DESCRIPTION, ERROR_URI}
    ));

    private final Map<String, String> errorMessages = new HashMap<>();
    
    public OAuthCredentialsException(final String message) {
        super(message);
    }
    
    public void setErrorMessage(final String name, final String message) {
        this.errorMessages.put(name, message);
    }
    
    public Map<String, String> getErrorMessages() {
        return this.errorMessages;
    }
    
    public String getError() {
        return this.errorMessages.get(ERROR);
    }
    
    public String getErrorReason() {
        return this.errorMessages.get(ERROR_REASON);
    }
    
    public String getErrorDescription() {
        return this.errorMessages.get(ERROR_DESCRIPTION);
    }
    
    public String getErrorUri() {
        return this.errorMessages.get(ERROR_URI);
    }
}
